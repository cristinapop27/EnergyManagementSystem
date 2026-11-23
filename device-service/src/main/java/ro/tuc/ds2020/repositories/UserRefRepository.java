package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ro.tuc.ds2020.entities.UserRef;

import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface UserRefRepository extends JpaRepository<UserRef, UUID> {
}
