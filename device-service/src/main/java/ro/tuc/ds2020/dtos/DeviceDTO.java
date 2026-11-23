package ro.tuc.ds2020.dtos;

import ro.tuc.ds2020.entities.EnergyClass;

import java.util.UUID;

public class DeviceDTO {

    private UUID id;
    private String name;
    private String description;
    private Double maxConsumption;
    private EnergyClass energyClass;
    private UUID userId;              // null when the device is not assigned

    public DeviceDTO() {
    }

    public DeviceDTO(UUID id, String name, String description,
                     Double maxConsumption, EnergyClass energyClass, UUID userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxConsumption = maxConsumption;
        this.energyClass = energyClass;
        this.userId = userId;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}