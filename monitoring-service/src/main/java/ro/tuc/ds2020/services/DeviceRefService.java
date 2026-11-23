package ro.tuc.ds2020.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.DeviceRef;
import ro.tuc.ds2020.repositories.DeviceRefRepository;
import ro.tuc.ds2020.repositories.HourlyConsumptionRepository;

import java.util.UUID;

@Service
public class DeviceRefService {

    private final DeviceRefRepository deviceRefRepository;
    private final HourlyConsumptionRepository hourlyConsumptionRepository;

    public DeviceRefService(DeviceRefRepository deviceRefRepository,
                            HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.deviceRefRepository = deviceRefRepository;
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }

    @Transactional
    public void save(UUID id, String name, UUID userId) {
        DeviceRef ref = deviceRefRepository.findById(id).orElseGet(() -> new DeviceRef(id));
        ref.setName(name);
        ref.setUserId(userId);
        deviceRefRepository.save(ref);
    }

    @Transactional
    public void delete(UUID id) {
        hourlyConsumptionRepository.deleteByDeviceId(id);
        deviceRefRepository.deleteById(id);
    }
}