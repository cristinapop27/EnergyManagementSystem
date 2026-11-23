package ro.tuc.ds2020.dtos;

import ro.tuc.ds2020.entities.Role;

import java.util.UUID;

public class UserDTO {
    private UUID id; private String name; private String username; private Role role;

    public UserDTO(UUID id, String name, String username, Role role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getUsername() {
        return username;
    }
    public Role getRole() {
        return role;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}
