package ro.tuc.ds2020.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.LoginRequest;
import ro.tuc.ds2020.dtos.LoginResponse;
import ro.tuc.ds2020.dtos.RegisterRequest;
import ro.tuc.ds2020.entities.Role;
import ro.tuc.ds2020.services.AuthService;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UUID> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request, Role.CLIENT), HttpStatus.CREATED);
    }

    @PostMapping("/admin/users")
    public ResponseEntity<UUID> createByAdmin(@Valid @RequestBody RegisterRequest request) {
        Role role = request.getRole() == null ? Role.CLIENT : request.getRole();
        return new ResponseEntity<>(authService.register(request, role), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // called by traefik forwardAuth
    @RequestMapping("/validate")
    public ResponseEntity<Void> validate(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Forwarded-Method", required = false) String method,
            @RequestHeader(value = "X-Forwarded-Uri", required = false) String uri) {

        AuthService.ValidationResult result = authService.validate(authorization, method, uri);

        ResponseEntity.BodyBuilder response = ResponseEntity.status(result.status());
        if (result.userId() != null) {
            response.header("X-User-Id", result.userId().toString());
            response.header("X-User-Role", result.role().name());
        }
        return response.build();
    }
}