package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.UserRefDTO;
import ro.tuc.ds2020.entities.UserRef;

public class UserRefBuilder {

    private UserRefBuilder() {
    }

    public static UserRef toEntity(UserRefDTO dto) {
        return new UserRef(dto.getId(), dto.getName(), dto.getUsername(), dto.getRole());
    }

    public static UserRefDTO toUserRefDTO(UserRef userRef) {
        return new UserRefDTO(userRef.getId(), userRef.getName(),
                userRef.getUsername(), userRef.getRole());
    }
}