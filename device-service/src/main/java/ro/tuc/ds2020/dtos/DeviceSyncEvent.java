package ro.tuc.ds2020.dtos;

import java.util.UUID;

public class DeviceSyncEvent {

    private String type;
    private UUID id;
    private String name;
    private UUID userId;

    public DeviceSyncEvent() {
    }

    public DeviceSyncEvent(String type, UUID id, String name, UUID userId) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "DeviceSyncEvent{type=" + type + ", id=" + id + ", name=" + name + ", userId=" + userId + "}";
    }
}