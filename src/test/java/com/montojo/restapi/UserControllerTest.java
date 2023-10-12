package com.montojo.restapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.montojo.restapi.api.UserController;
import com.montojo.restapi.dto.UserDTO;
import com.montojo.restapi.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private static List<UserDTO> userDTOList;
    @InjectMocks
    UserController userController;

    @Mock
    private UserService userService;

    @BeforeAll
    public static void userDTOListInit() {
        userDTOList = new ArrayList<>(List.of(
            new UserDTO("username1", "name1", "email1@email.com", "male", "http://picture_1.com/here"),
            new UserDTO("username2", "name2", "email2@email.com", "female", "http://picture_2.com/here"),
            new UserDTO("username3", "name3", "email3@email.com", "male", "http://picture_3.com/here")
        ));
    }

    @Test
    public void getUserByIdTest() throws Exception {

        Mockito.when(userService.getUserById("username1"))
            .thenReturn(userDTOList.get(0));
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserDTO resultUserDTO = userController.getUserById("username1");

        assertThat(resultUserDTO.getName()).isEqualTo("name1");
        assertThat(resultUserDTO.getGender()).isEqualTo("male");
    }


    @Test
    public void getAllUsersTest() throws Exception {

        Mockito.when(userService.getAllUsers())
            .thenReturn(userDTOList);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<List<UserDTO>> responseEntity = userController.getAllUsers(null, null);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<UserDTO> allUsersDTOList = responseEntity.getBody();
        assertThat(allUsersDTOList.get(1).getName()).isEqualTo("name2");
    }
}
