package com.montojo.restapi.api;

import com.montojo.restapi.services.UserGeneratorService;
import com.montojo.restapi.dto.UserDTO;
import com.montojo.restapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users/")
@Tag(name = "User Controller")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserGeneratorService userGeneratorService;
    private final UserService userService;

    @Autowired
    public UserController(UserGeneratorService userGeneratorService, UserService userService) {
        this.userGeneratorService = userGeneratorService;
        this.userService = userService;
    }

    @GetMapping("{username}/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by username", description = "Retrieves single user by unique username", parameters = {@Parameter(name = "username", required = true, in = ParameterIn.PATH)})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Parameter not provided or not valid ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LinkedHashMap.class))}),
        @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LinkedHashMap.class))})
    })
    public UserDTO getUserById(@PathVariable(name = "username", required = true) String userName) {
        LOGGER.info("Delegating to retrieve user with username: {}.", userName);
        return userService.getUserById(userName);
    }

    @GetMapping("")
    @Operation(summary = "Get all users", description = "Retrieves all users. If page and size paramenters are provided, then response is paginated, otherwise all users are included", parameters = {})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All users retrieved successfully", content = {@Content(mediaType = "application/json", schema = @Schema(name = "List of UserDTO objects", implementation = List.class))}),
        @ApiResponse(responseCode = "202", description = "All users retrieved successfully with pagination", content = {@Content(mediaType = "application/json", schema = @Schema(name = "Page object with UserDTO objects as content", implementation = Page.class))}),
        @ApiResponse(responseCode = "404", description = "No users found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LinkedHashMap.class))})
    })
    public ResponseEntity getAllUsers(@RequestParam(name = "page", required = false) String page, @RequestParam(name = "size", required = false) String size) {
        LOGGER.info("Delegating to retrieve all users.");
        if (!Objects.isNull(page) && !Objects.isNull(size)) {
            LOGGER.info("Page number {} and size {}", page, size);
            return ResponseEntity.status(202).body(userService.getAllUsersPaginated(Integer.valueOf(page), Integer.valueOf(size)));
        }
        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new user into db", description = "Saves a new user into db, received as json", parameters = {})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "New user successfully saved", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Missing required fields in JSON payload", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LinkedHashMap.class))}),
        @ApiResponse(responseCode = "422", description = "Payload doesn't comply with JSON format", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LinkedHashMap.class))}),
        @ApiResponse(responseCode = "422", description = "No payload in request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LinkedHashMap.class))})
    })
    public UserDTO saveUser(@Valid @RequestBody(required = true) UserDTO userDTO) {
        LOGGER.info("Delegating to save new user: {}.", userDTO);
        return userService.saveUser(userDTO);
    }

    @PutMapping(value = "{username}/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable(name = "username", required = true) String userName, @Valid @RequestBody(required = true) UserDTO userDTO) {
        LOGGER.info("Delegating to update user: {}.", userDTO);
        return userService.updateUser(userName, userDTO);
    }

    @DeleteMapping("{username}/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete user from db", description = "Tries to delete an user from db by id", parameters = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User sucessfully deleted"),
            @ApiResponse(responseCode = "400", description = "User to be deleted non existing", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}),
    })
    public void deleteUser(@PathVariable(name = "username", required = true) String userName) {
        LOGGER.info("Delegating to delete user with username: {}.", userName);
        userService.deleteUserById(userName);
    }

    @GetMapping("/generate/{number}/")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> generateUsers(@Valid @PathVariable(name = "number", required = true) Integer number) {
        LOGGER.info("Delegating to generate {} number of users.", number);
        var resultsRandomUser = userGeneratorService.generateUsers(number);
        return userService.saveRandomUsers(resultsRandomUser.getResults());
    }

    @GetMapping("/tree")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> treeOfUsers(@PathVariable(name = "number", required = true) String number) {
        LOGGER.info("Delegating to crete tree of users");
        return Arrays.asList(
            new UserDTO("username 1", "name 1", "email1@email.com", "male", "http://picture1.com/somewhere-here"),
            new UserDTO("username 2", "name 2", "email2@email.com", "female", "http://picture2.com/somewhere-here"),
            new UserDTO("username 3", "name 3", "email3@email.com", "male", "http://picture3.com/somewhere-here"));
    }
}
