package com.montojo.restapi.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.montojo.restapi.api.UserController;
import com.montojo.restapi.config.SecurityConfig;
import com.montojo.restapi.dto.UserDTO;
import com.montojo.restapi.services.UserGeneratorService;
import com.montojo.restapi.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
//@AutoConfigureMockMvc(addFilters = true)
public class UserControllerTest {

    private static final List<UserDTO> LIST_USERDTOS = List.of(
            new UserDTO("username1", "name1", "email1@email.com", "male", "http://picture_1.com/here"),
            new UserDTO("username2", "name2", "email2@email.com", "female", "http://picture_2.com/here"),
            new UserDTO("username3", "name3", "email3@email.com", "male", "http://picture_3.com/here")
    );
    private static final String apikeyName = "X-API-KEY";
    private static final String apisecretName = "X-API-SECRET";
    @Value("${api.key}")
    private String apiKey;
    @Value("${api.secret}")
    private String apiSecret;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserGeneratorService userGeneratorService;
    @MockBean
    private UserService userService;

    @Test
    public void getAllEmployees_getsAll() throws Exception {
        when(userService.getAllUsers()).thenReturn(LIST_USERDTOS);
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .get("/api/users")
                        .header(apikeyName, apiKey)
                        .header(apisecretName, apiSecret)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        System.out.println("************");
        System.out.println(apikeyName);
        System.out.println(apiKey);

        List<UserDTO> actualUserDTOS = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(actualUserDTOS).hasSameElementsAs(LIST_USERDTOS);
    }

    @Test
    public void postNewEmployee_createsNew() throws Exception {
        UserDTO userDTO = LIST_USERDTOS.get(0);
        when(userService.saveUser(userDTO)).thenReturn(userDTO);
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .header(apikeyName, apiKey)
                        .header(apisecretName, apiSecret)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        UserDTO savedUserDTO = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsString(),
                UserDTO.class);

        assertThat(savedUserDTO).isEqualTo(userDTO);
    }

    @Test
    public void deleteExistingEmployee_deleteSuccessful() throws Exception {
        UserDTO userDTO = LIST_USERDTOS.get(0);
        doNothing().when(userService).deleteUserById(userDTO.getUserName());
        mvc.perform(MockMvcRequestBuilders
                        .delete(String.format("/api/users/%s", userDTO.getUserName()))
                        .header(apikeyName, apiKey)
                        .header(apisecretName, apiSecret)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNonExistingEmployee_deleteUnsuccessful() throws Exception {
        UserDTO userDTO = LIST_USERDTOS.get(0);
        doThrow(new IllegalArgumentException("Username not in database, nothing was deleted."))
                .when(userService)
                .deleteUserById(userDTO.getUserName());
        mvc.perform(MockMvcRequestBuilders
                        .delete(String.format("/api/users/%s", userDTO.getUserName()))
                )
                .andExpect(status().is4xxClientError());
    }
}
