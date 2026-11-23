package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ro.tuc.ds2020.entities.Device;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    List<Device> findByUser_Id(UUID userId);
}