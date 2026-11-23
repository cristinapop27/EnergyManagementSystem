package ro.tuc.ds2020.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ro.tuc.ds2020.entities.EnergyClass;

public class DeviceDetailsDTO {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Positive
    private Double maxConsumption;

    @NotNull
    private EnergyClass energyClass;

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
}