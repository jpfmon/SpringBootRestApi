package com.montojo.restapi.services;

import com.randomuser.types.Name;
import com.randomuser.types.Picture;
import com.randomuser.types.RandomUser;
import com.randomuser.types.ResultsRandomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserGeneratorServiceTest {
    @InjectMocks
    UserGeneratorService userGeneratorService;
    @Mock
    private RestTemplate restTemplate;
    private ResultsRandomUser resultsRandomUser;

    @BeforeEach
    public void generateRandomUsersList() throws URISyntaxException {
        RandomUser.RandomUserBuilderBase randomUser = RandomUser.builder()
                .withName(Name.builder().withLast("gen_user1").build())
                .withEmail("email1@email.com")
                .withGender("male")
                .withPicture(Picture.builder().withLarge(new URI("http://picture_1.com/here")).build());

        resultsRandomUser = ResultsRandomUser.builder().withResults(List.of(randomUser)).build();
    }

    @Test
    public void generateUsers_Success() {
        when(restTemplate.getForObject(eq("https://randomuser.me/api/?inc=name,gender,email,picture&results=1"),
                eq(ResultsRandomUser.class))).thenReturn(resultsRandomUser);

        ResultsRandomUser actualResultsRandomUser = userGeneratorService.generateUsers(1);

        assertThat(actualResultsRandomUser.getResults()).hasSameElementsAs(resultsRandomUser.getResults());
    }
}
