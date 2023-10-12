package com.montojo.restapi.mapper;

import com.montojo.restapi.dto.UserDTO;
import com.montojo.restapi.model.User;
import com.randomuser.types.RandomUser;

public interface UserMapper {

    User mapDTOtoEntity(UserDTO userDTO);

    UserDTO mapEntityToDTO(User user);

    User mapRandomUserToEntity(RandomUser randomUser);
}
