package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.tuc.ds2020.entities.Credential;
import ro.tuc.ds2020.entities.Role;

import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository extends JpaRepository<Credential, UUID> {

    Optional<Credential> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByRole(Role role);
}