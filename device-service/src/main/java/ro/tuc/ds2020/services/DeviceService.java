package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.Role;
import ro.tuc.ds2020.entities.UserRef;
import ro.tuc.ds2020.messaging.DeviceEventPublisher;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.UserRefRepository;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;
    private final UserRefRepository userRefRepository;
    private final DeviceEventPublisher deviceEventPublisher;

    public DeviceService(DeviceRepository deviceRepository, UserRefRepository userRefRepository,
                         DeviceEventPublisher deviceEventPublisher) {
        this.deviceRepository = deviceRepository;
        this.userRefRepository = userRefRepository;
        this.deviceEventPublisher = deviceEventPublisher;
    }

    @Transactional(readOnly = true)
    public List<DeviceDTO> findDevices() {
        return deviceRepository.findAll().stream()
                .map(DeviceBuilder::toDeviceDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceDTO findDeviceById(UUID id) {
        return DeviceBuilder.toDeviceDTO(getDeviceOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<DeviceDTO> findDevicesByUser(UUID userId) {
        if (!userRefRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id: " + userId);
        }
        return deviceRepository.findByUser_Id(userId).stream()
                .map(DeviceBuilder::toDeviceDTO)
                .toList();
    }

    @Transactional
    public UUID insert(DeviceDetailsDTO dto) {
        Device device = deviceRepository.save(DeviceBuilder.toEntity(dto));
        deviceEventPublisher.publishCreated(device.getId(), device.getName());
        return device.getId();
    }

    @Transactional
    public DeviceDTO update(UUID id, DeviceDetailsDTO dto) {
        Device device = getDeviceOrThrow(id);
        device.setName(dto.getName());
        device.setDescription(dto.getDescription());
        device.setMaxConsumption(dto.getMaxConsumption());
        device.setEnergyClass(dto.getEnergyClass());
        return saveAndPublish(device);
    }

    @Transactional
    public void delete(UUID id) {
        deviceRepository.delete(getDeviceOrThrow(id));
        deviceEventPublisher.publishDeleted(id);
    }

    @Transactional
    public DeviceDTO assignToUser(UUID deviceId, UUID userId) {
        Device device = getDeviceOrThrow(deviceId);
        UserRef user = userRefRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + userId));

        device.setUser(user);
        return saveAndPublish(device);
    }

    @Transactional
    public DeviceDTO unassign(UUID deviceId) {
        Device device = getDeviceOrThrow(deviceId);
        device.setUser(null);
        return saveAndPublish(device);
    }

    private DeviceDTO saveAndPublish(Device device) {
        Device saved = deviceRepository.save(device);
        deviceEventPublisher.publishUpdated(saved.getId(), saved.getName(),
                saved.getUser() == null ? null : saved.getUser().getId());
        return DeviceBuilder.toDeviceDTO(saved);
    }

    private Device getDeviceOrThrow(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device with id: " + id));
    }
}