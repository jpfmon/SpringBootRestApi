package com.montojo.restapi.mapper.impl;

import com.montojo.restapi.dto.UserDTO;
import com.montojo.restapi.mapper.UserMapper;
import com.montojo.restapi.model.User;
import com.randomuser.types.RandomUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    public User mapDTOtoEntity(UserDTO userDTO) {
        return new User(userDTO.getUserName(), userDTO.getName(), userDTO.getEmail(), userDTO.getGender(), userDTO.getPictureURL());
    }

    public UserDTO mapEntityToDTO(User user) {
        return new UserDTO(user.getUserName(), user.getName(), user.getEmail(), user.getGender(), user.getPictureURL());
    }

    public User mapRandomUserToEntity(RandomUser randomUser) {
        String userName = randomUser.getName().getFirst() + randomUser.getName().getLast();
        String name = randomUser.getName().getFirst() + " " + randomUser.getName().getLast();
        return new User(userName,name, randomUser.getEmail(), randomUser.getGender(), randomUser.getPicture().getLarge().toString());
    }
}
