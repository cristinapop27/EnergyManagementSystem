package ro.tuc.ds2020.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class MeasurementMessage {

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("device_id")
    private UUID deviceId;

    @JsonProperty("measurement_value")
    private Double measurementValue;

    public MeasurementMessage() {
    }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public Double getMeasurementValue() { return measurementValue; }
    public void setMeasurementValue(Double measurementValue) { this.measurementValue = measurementValue; }

    @Override
    public String toString() {
        return "Measurement{timestamp=" + timestamp + ", device_id=" + deviceId
                + ", measurement_value=" + measurementValue + "}";
    }
}
