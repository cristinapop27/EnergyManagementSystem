package ro.tuc.ds2020.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
public class Device {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double maxConsumption;

    @Enumerated(EnumType.STRING)
    private EnergyClass energyClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserRef user;

    public Device() {}

    public Device(String name, String description, Double maxConsumption, EnergyClass en){
        this.name = name;
        this.description = description;
        this.maxConsumption = maxConsumption;
        this.energyClass = en;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(Double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public EnergyClass getEnergyClass() {
        return energyClass;
    }

    public void setEnergyClass(EnergyClass energyClass) {
        this.energyClass = energyClass;
    }

    public UserRef getUser() {
        return user;
    }

    public void setUser(UserRef user) {
        this.user = user;
    }
}