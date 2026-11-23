package ro.tuc.ds2020.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ForbiddenException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.HourlyConsumptionDTO;
import ro.tuc.ds2020.entities.DeviceRef;
import ro.tuc.ds2020.entities.HourlyConsumption;
import ro.tuc.ds2020.repositories.DeviceRefRepository;
import ro.tuc.ds2020.repositories.HourlyConsumptionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConsumptionService {

    private final DeviceRefRepository deviceRefRepository;
    private final HourlyConsumptionRepository hourlyConsumptionRepository;

    public ConsumptionService(DeviceRefRepository deviceRefRepository,
                              HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.deviceRefRepository = deviceRefRepository;
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }

    @Transactional(readOnly = true)
    public List<HourlyConsumptionDTO> getDaily(UUID deviceId, LocalDate date, UUID callerId, String callerRole) {
        DeviceRef device = deviceRefRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device with id: " + deviceId));

        if (!"ADMIN".equals(callerRole) && !callerId.equals(device.getUserId())) {
            throw new ForbiddenException("Device with id: " + deviceId);
        }

        LocalDateTime from = date.atStartOfDay();
        double[] kwh = new double[24];
        for (HourlyConsumption h : hourlyConsumptionRepository
                .findByDeviceIdAndHourStartBetween(deviceId, from, from.plusHours(23))) {
            kwh[h.getHourStart().getHour()] = h.getTotalKwh();
        }

        List<HourlyConsumptionDTO> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            result.add(new HourlyConsumptionDTO(hour, kwh[hour]));
        }
        return result;
    }
}