package ro.tuc.ds2020.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.Role;
import ro.tuc.ds2020.repositories.CredentialRepository;

import java.util.UUID;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;

    public CredentialService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Transactional
    public void update(UUID id, String username, Role role) {
        credentialRepository.findById(id).ifPresent(credential -> {
            credential.setUsername(username);
            credential.setRole(role);
        });
    }

    @Transactional
    public void deleteIfPresent(UUID id) {
        if (credentialRepository.existsById(id)) {
            credentialRepository.deleteById(id);
        }
    }
}
