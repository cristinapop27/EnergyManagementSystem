package ro.tuc.ds2020.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.tuc.ds2020.entities.Role;

import java.util.UUID;

public class UserRefDTO {

    @NotNull
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @NotNull
    private Role role;

    public UserRefDTO() {
    }

    public UserRefDTO(UUID id, String name, String username, Role role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
