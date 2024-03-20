package com.montojo.restapi.services;

import com.montojo.restapi.dto.UserDTO;
import com.montojo.restapi.mapper.UserMapper;
import com.montojo.restapi.model.User;
import com.montojo.restapi.repository.UserRepository;
import com.montojo.restapi.repository.impl.UsernamesCacheImpl;
import com.randomuser.types.RandomUser;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private UsernamesCacheImpl usernamesCacheImpl;
    private final Predicate<User> checkUsersAndLogWarningPredicate = (user) -> {
        if (usernamesCacheImpl.isUsernameInCache(user.getUserName())) {
            LOGGER.warn("Can't save user: {} , username already present in DB", user);
            return false;
        }
        addToCache(user.getUserName());
        LOGGER.warn("Size of cache {}", usernamesCacheImpl.cacheSize());
        return true;
    };


    @Autowired
    public UserService(UserMapper userMapper, UsernamesCacheImpl usernamesCacheImpl, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.usernamesCacheImpl = usernamesCacheImpl;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void cacheInitFromDB() {
        userRepository.findAll().forEach((user) -> usernamesCacheImpl.addUsernameToCache(user.getUserName()));
        LOGGER.info("Cache initialized, size: {}", usernamesCacheImpl.cacheSize());
    }

    public UserDTO getUserById(String userName) {
        var optionalUserDTO = userRepository.findById(userName).map((userMapper::mapEntityToDTO));
        if (optionalUserDTO.isPresent()) return optionalUserDTO.get();
        throw new NoSuchElementException(userName);
    }

    public List<UserDTO> getAllUsers() {
        var userEntities = userRepository.findAll();
        var userDTOList = userEntities.stream().map(userMapper::mapEntityToDTO)
                .collect(Collectors.toList());
        LOGGER.info("Number of users found: {}", userDTOList.size());
        return userDTOList;
    }

    public Page<UserDTO> getAllUsersPaginated(Integer page, Integer size) {
        var pageable = PageRequest.of(page, size);
        var userEntities = userRepository.findAll(pageable);
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserDTO> dtoPage = userPage.map(userMapper::mapEntityToDTO);
        var userDTOList = StreamSupport.stream(userEntities.spliterator(), false).map(userMapper::mapEntityToDTO)
                .toList();
        LOGGER.info("Number of users found: {}", userDTOList.size());
        return dtoPage;
    }

    public UserDTO saveUser(UserDTO userDTO) {
        LOGGER.info("Saving user: {}", userDTO);
        if (!isUserAlreadySaved(userDTO.getUserName())) {
            addToCache(userDTO.getUserName());
            return userMapper.mapEntityToDTO(userRepository.save(userMapper.mapDTOtoEntity(userDTO)));
        }
        throw new IllegalArgumentException("User not saved: Username already present in database.");
    }

    public UserDTO updateUser(String userName, UserDTO userDTO) {
        if (isUserAlreadySaved(userName)) {
            LOGGER.info("Saving user: {}", userDTO);
            return userMapper.mapEntityToDTO(userRepository.save(userMapper.mapDTOtoEntity(userDTO)));
        }
        throw new IllegalArgumentException("User not updated: path parameter User is not present in database.");
    }

    public void deleteUserById(String username) {
        if (isUserAlreadySaved(username)) {
            userRepository.deleteById(username);
            usernamesCacheImpl.removeUsernameFromCache(username);
        } else {
            throw new IllegalArgumentException("Username not in database, nothing was deleted.");
        }
    }

    public List<UserDTO> saveRandomUsers(List<RandomUser> randomUserList) {
        LOGGER.info("Number of randomUsers to be saved: {}", randomUserList.size());
        var users = randomUserList.stream().map(userMapper::mapRandomUserToEntity).filter(checkUsersAndLogWarningPredicate).toList();
        List<UserDTO> savedUsers = userRepository.saveAll(users).stream().map(userMapper::mapEntityToDTO).toList();
        compareListSizes(randomUserList, savedUsers);
        return savedUsers;
    }

    private boolean isUserAlreadySaved(String username) {
        try {
            getUserById(username);
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    private void compareListSizes(List<RandomUser> listToBeSaved, List<UserDTO> listAlreadySaved) {
        int difference = listToBeSaved.size() - listAlreadySaved.size();
        if (difference != 0) {
            LOGGER.warn("Not all users were saved, their usernames were already present in db. Number of users not saved: {}", difference);
        } else {
            LOGGER.info("All users successfully saved into db.");
        }
    }

    private void addToCache(String username) {
        usernamesCacheImpl.addUsernameToCache(username);
        LOGGER.warn("Added username {} to repeatedRecordsCache", username);
    }
}
