package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.tuc.ds2020.entities.DeviceRef;

import java.util.UUID;

public interface DeviceRefRepository extends JpaRepository<DeviceRef, UUID> {
}