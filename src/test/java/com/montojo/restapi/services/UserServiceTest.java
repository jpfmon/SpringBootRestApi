package com.montojo.restapi.services;

import com.montojo.restapi.dto.UserDTO;
import com.montojo.restapi.mapper.impl.UserMapperImpl;
import com.montojo.restapi.model.User;
import com.montojo.restapi.repository.UserRepository;
import com.montojo.restapi.repository.impl.UsernamesCacheImpl;
import com.montojo.restapi.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService userService;
    private List<UserDTO> userDTOList = new ArrayList<>(List.of(
            new UserDTO("username1", "name1", "email1@email.com", "male", "http://picture_1.com/here"),
            new UserDTO("username2", "name2", "email2@email.com", "female", "http://picture_2.com/here"),
            new UserDTO("username3", "name3", "email3@email.com", "male", "http://picture_3.com/here")
    ));
    private List<User> userList = new ArrayList<>(List.of(
            new User("username1", "name1", "email1@email.com", "male", "http://picture_1.com/here"),
            new User("username2", "name2", "email2@email.com", "female", "http://picture_2.com/here"),
            new User("username3", "name3", "email3@email.com", "male", "http://picture_3.com/here")
    ));
    @Mock
    private UserMapperImpl userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UsernamesCacheImpl usernamesCache;

    @Test
    public void getAllUsersTest_GetsAll() throws Exception {
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.mapEntityToDTO(any())).thenCallRealMethod();

        List<UserDTO> actualUsersList = userService.getAllUsers();

        assertThat(actualUsersList).hasSameElementsAs(userDTOList);
    }

    @Test
    public void getUserByIdTest_ExistingUser_GetsUser() throws Exception {
        User user = userList.get(0);
        String username = user.getUserName();
        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(userMapper.mapEntityToDTO(any())).thenCallRealMethod();

        UserDTO actualUserDTO = userService.getUserById(username);
        UserDTO expectedUserDTO = userMapper.mapEntityToDTO(user);

        assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
    }

    @Test
    public void saveUser_ValidUser_Successful() {
        UserDTO userDTO = userDTOList.get(0);
        User user = userList.get(0);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapEntityToDTO(any())).thenCallRealMethod();
        when(userMapper.mapDTOtoEntity(any())).thenCallRealMethod();

        UserDTO savedUserDTO = userService.saveUser(userDTO);

        assertThat(savedUserDTO).isEqualTo(userDTO);
    }

    @Test
    public void saveUser_AttemptsSavingExistingUser_Unsuccessful() {
        UserDTO userDTO = userDTOList.get(0);
        User user = userList.get(0);

        when(userRepository.findById(user.getUserName())).thenReturn(Optional.of(user));
        when(userMapper.mapEntityToDTO(any())).thenCallRealMethod();
        lenient().when(userMapper.mapDTOtoEntity(any())).thenCallRealMethod();


        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(userDTO));
    }
}
