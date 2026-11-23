package ro.tuc.ds2020.dtos;

import ro.tuc.ds2020.entities.Role;

import java.util.UUID;

public class LoginResponse {

    private final String token;
    private final UUID userId;
    private final Role role;

    public LoginResponse(String token, UUID userId, Role role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() { return token; }
    public UUID getUserId() { return userId; }
    public Role getRole() { return role; }
}