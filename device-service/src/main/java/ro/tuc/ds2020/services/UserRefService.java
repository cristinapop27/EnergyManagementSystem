package ro.tuc.ds2020.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.DuplicateResourceException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.UserRefDTO;
import ro.tuc.ds2020.dtos.builders.UserRefBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.UserRef;
import ro.tuc.ds2020.messaging.DeviceEventPublisher;
import ro.tuc.ds2020.repositories.UserRefRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserRefService {

    private final UserRefRepository userRefRepository;
    private final DeviceEventPublisher deviceEventPublisher;

    public UserRefService(UserRefRepository userRefRepository, DeviceEventPublisher deviceEventPublisher) {
        this.userRefRepository = userRefRepository;
        this.deviceEventPublisher = deviceEventPublisher;
    }

    @Transactional(readOnly = true)
    public List<UserRefDTO> findAll() {
        return userRefRepository.findAll().stream()
                .map(UserRefBuilder::toUserRefDTO)
                .toList();
    }

    @Transactional
    public UUID create(UserRefDTO dto) {
        if (userRefRepository.existsById(dto.getId())) {
            throw new DuplicateResourceException("UserRef with id: " + dto.getId());
        }
        return userRefRepository.save(UserRefBuilder.toEntity(dto)).getId();
    }

    @Transactional
    public UserRefDTO update(UUID id, UserRefDTO dto) {
        UserRef userRef = getOrThrow(id);
        userRef.setName(dto.getName());
        userRef.setUsername(dto.getUsername());
        userRef.setRole(dto.getRole());
        return UserRefBuilder.toUserRefDTO(userRefRepository.save(userRef));
    }

    @Transactional
    public void delete(UUID id) {
        UserRef userRef = getOrThrow(id);
        for (Device device : userRef.getDevices()) {
            deviceEventPublisher.publishDeleted(device.getId());
        }
        userRefRepository.delete(userRef);
    }

    private UserRef getOrThrow(UUID id) {
        return userRefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserRef with id: " + id));
    }

    @Transactional
    public void upsert(UserRefDTO dto) {
        UserRef ref = userRefRepository.findById(dto.getId()).orElse(null);
        if (ref == null) {
            userRefRepository.save(UserRefBuilder.toEntity(dto));
            return;
        }
        ref.setName(dto.getName());
        ref.setUsername(dto.getUsername());
        ref.setRole(dto.getRole());
    }

    @Transactional
    public void deleteIfPresent(UUID id) {
        if (userRefRepository.existsById(id)) {
            delete(id);
        }
    }
}
