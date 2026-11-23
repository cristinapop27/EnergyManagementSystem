package ro.tuc.ds2020.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.dtos.MeasurementMessage;
import ro.tuc.ds2020.entities.HourlyConsumption;
import ro.tuc.ds2020.repositories.HourlyConsumptionRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class MeasurementService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Bucharest");

    private final HourlyConsumptionRepository repository;

    public MeasurementService(HourlyConsumptionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public HourlyConsumption process(MeasurementMessage m) {
        LocalDateTime hourStart = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(m.getTimestamp()), ZONE)
                .truncatedTo(ChronoUnit.HOURS);

        HourlyConsumption hour = repository
                .findByDeviceIdAndHourStart(m.getDeviceId(), hourStart)
                .orElseGet(() -> new HourlyConsumption(m.getDeviceId(), hourStart));

        hour.add(m.getMeasurementValue());
        return repository.save(hour);
    }
}
