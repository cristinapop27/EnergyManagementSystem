package ro.tuc.ds2020.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "device_ref")
public class DeviceRef {

    @Id
    private UUID id;

    private String name;

    private UUID userId;

    protected DeviceRef() {
    }

    public DeviceRef(UUID id) {
        this.id = id;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
}