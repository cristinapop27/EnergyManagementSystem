package ro.tuc.ds2020.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.RegisterRequest;
import ro.tuc.ds2020.entities.Role;
import ro.tuc.ds2020.repositories.CredentialRepository;
import ro.tuc.ds2020.services.AuthService;

@Component
public class AdminSeeder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSeeder.class);

    private final CredentialRepository credentialRepository;
    private final AuthService authService;
    private final AmqpAdmin amqpAdmin;
    private final String username;
    private final String password;

    public AdminSeeder(CredentialRepository credentialRepository, AuthService authService, AmqpAdmin amqpAdmin,
                       @Value("${admin.username}") String username,
                       @Value("${admin.password}") String password) {
        this.credentialRepository = credentialRepository;
        this.authService = authService;
        this.amqpAdmin = amqpAdmin;
        this.username = username;
        this.password = password;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedAdmin() throws InterruptedException {
        if (credentialRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        for (int attempt = 1; attempt <= 20; attempt++) {
            try {
                if (amqpAdmin.getQueueProperties("sync.user-service") == null
                        || amqpAdmin.getQueueProperties("sync.device-service") == null) {
                    LOGGER.info("Admin seeding attempt {}: waiting for user-service and device-service queues", attempt);
                    Thread.sleep(5000);
                    continue;
                }
                authService.register(new RegisterRequest("Administrator", username, password, Role.ADMIN),
                        Role.ADMIN);
                LOGGER.info("Seeded admin user '{}'", username);
                return;
            } catch (RuntimeException e) {
                LOGGER.warn("Admin seeding attempt {} failed: {}", attempt, e.getMessage());
                Thread.sleep(5000);
            }
        }
        LOGGER.error("Could not seed admin user after 20 attempts");
    }
}