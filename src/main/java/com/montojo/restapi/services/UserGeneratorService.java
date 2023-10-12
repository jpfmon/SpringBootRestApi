package com.montojo.restapi.services;

import com.randomuser.types.ResultsRandomUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGeneratorService.class);
    private static final String URI = "https://randomuser.me/api/?inc=name,gender,email,picture&results=";

    public ResultsRandomUser generateUsers(Integer number) {
        RestTemplate restTemplate = new RestTemplate();
        ResultsRandomUser result = restTemplate.getForObject(URI + number, ResultsRandomUser.class);
        LOGGER.debug("Result of Rest template: {}", result);
        return result;
    }
}
