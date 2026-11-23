package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserDetailsDTO;
import ro.tuc.ds2020.entities.User;

public class UserBuilder {

    public static UserDTO toUserDTO(User user){
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRole());
    }

    public static User toEntity(UserDetailsDTO userDTO){
        return new User(
                userDTO.getName(),
                userDTO.getUsername(),
                userDTO.getRole()
        );
    }
}
