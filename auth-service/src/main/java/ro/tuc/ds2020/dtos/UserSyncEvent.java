package ro.tuc.ds2020.dtos;

import ro.tuc.ds2020.entities.Role;

import java.util.UUID;

public class UserSyncEvent {

    private String type;
    private UUID id;
    private String name;
    private String username;
    private Role role;

    public UserSyncEvent() {
    }

    public UserSyncEvent(String type, UUID id, String name, String username, Role role) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return "UserSyncEvent{type=" + type + ", id=" + id + ", username=" + username + ", role=" + role + "}";
    }
}
