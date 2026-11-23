package ro.tuc.ds2020.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.DuplicateResourceException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.InvalidCredentialsException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.SyncFailedException;
import ro.tuc.ds2020.dtos.LoginRequest;
import ro.tuc.ds2020.dtos.LoginResponse;
import ro.tuc.ds2020.dtos.RegisterRequest;
import ro.tuc.ds2020.entities.Credential;
import ro.tuc.ds2020.entities.Role;
import ro.tuc.ds2020.messaging.UserEventPublisher;
import ro.tuc.ds2020.repositories.CredentialRepository;
import ro.tuc.ds2020.security.AccessRules;
import ro.tuc.ds2020.security.JwtService;

import java.util.UUID;

@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final CredentialRepository credentialRepository;
    private final UserEventPublisher userEventPublisher;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AccessRules accessRules;

    public AuthService(CredentialRepository credentialRepository, UserEventPublisher userEventPublisher,
                       BCryptPasswordEncoder passwordEncoder, JwtService jwtService,
                       AccessRules accessRules) {
        this.credentialRepository = credentialRepository;
        this.userEventPublisher = userEventPublisher;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.accessRules = accessRules;
    }

    @Transactional
    public UUID register(RegisterRequest request, Role role) {
        if (credentialRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User with username: " + request.getUsername());
        }

        UUID userId = UUID.randomUUID();
        String hash = passwordEncoder.encode(request.getPassword());
        credentialRepository.save(new Credential(userId, request.getUsername(), hash, role));

        try {
            userEventPublisher.publishUserCreated(userId, request.getName(), request.getUsername(), role);
        } catch (AmqpException e) {
            LOGGER.error("Could not publish user.created for {}: {}", userId, e.getMessage());
            throw new SyncFailedException("User");
        }

        return userId;
    }

    public LoginResponse login(LoginRequest request) {
        Credential credential = credentialRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), credential.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generate(credential);
        return new LoginResponse(token, credential.getId(), credential.getRole());
    }

    public ValidationResult validate(String authorizationHeader, String method, String uri) {
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return ValidationResult.allowed(null, null);
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ValidationResult.status(HttpStatus.UNAUTHORIZED);
        }

        Claims claims;
        try {
            claims = jwtService.parse(authorizationHeader.substring(7));   // after "Bearer "
        } catch (JwtException | IllegalArgumentException e) {
            return ValidationResult.status(HttpStatus.UNAUTHORIZED);
        }

        UUID userId = UUID.fromString(claims.getSubject());
        Role role = Role.valueOf(claims.get("role", String.class));

        if (!accessRules.isAllowed(role, userId, method, uri)) {
            return ValidationResult.status(HttpStatus.FORBIDDEN);
        }
        return ValidationResult.allowed(userId, role);
    }

    public record ValidationResult(HttpStatus status, UUID userId, Role role) {

        static ValidationResult status(HttpStatus status) {
            return new ValidationResult(status, null, null);
        }

        static ValidationResult allowed(UUID userId, Role role) {
            return new ValidationResult(HttpStatus.OK, userId, role);
        }
    }
}