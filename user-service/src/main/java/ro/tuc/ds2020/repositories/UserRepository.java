package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ro.tuc.ds2020.entities.User;

import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);
}
