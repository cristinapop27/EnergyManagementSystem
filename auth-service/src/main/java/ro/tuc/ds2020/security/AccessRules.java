package ro.tuc.ds2020.security;

import org.springframework.stereotype.Component;
import ro.tuc.ds2020.entities.Role;

import java.util.UUID;

@Component
public class AccessRules {

    public boolean isAllowed(Role role, UUID userId, String method, String uri) {
        String path = uri == null ? "" : uri.split("\\?")[0];
        String m = method == null ? "GET" : method.toUpperCase();

        //creation goes through /api/auth/register or /api/auth/admin/users
        if (m.equals("POST") && path.equals("/api/users")) {
            return false;
        }

        // ADMIN: everything else
        if (role == Role.ADMIN) {
            return true;
        }

        // CLIENT: only read their own profile and their own devices
        if (m.equals("GET")) {
            return path.equals("/api/devices/user/" + userId)
                            || path.equals("/api/users/" + userId)
                            || path.startsWith("/api/monitoring/devices/");
        }
        return false;
    }
}