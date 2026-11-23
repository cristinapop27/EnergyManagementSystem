package ro.tuc.ds2020.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hourly_consumption",
       uniqueConstraints = @UniqueConstraint(columnNames = {"device_id", "hour_start"}))
public class HourlyConsumption {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "hour_start", nullable = false)
    private LocalDateTime hourStart;

    @Column(name = "total_kwh", nullable = false)
    private Double totalKwh;

    protected HourlyConsumption() {
    }

    public HourlyConsumption(UUID deviceId, LocalDateTime hourStart) {
        this.deviceId = deviceId;
        this.hourStart = hourStart;
        this.totalKwh = 0.0;
    }

    public void add(double kwh) {
        this.totalKwh += kwh;
    }

    public UUID getId() { return id; }
    public UUID getDeviceId() { return deviceId; }
    public LocalDateTime getHourStart() { return hourStart; }
    public Double getTotalKwh() { return totalKwh; }
}
