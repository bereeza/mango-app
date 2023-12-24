package backend.mangoapp.controller;

import backend.mangoapp.entity.User;
import backend.mangoapp.service.userService.UserService;
import org.junit.jupiter.api.Assertions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User("bob@gmail.com", "12345", "@bob");
    }

    @Test
    public void testSignUp() {
        when(userService.add(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = restTemplate.postForEntity("/", user, User.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Assertions.assertEquals("bob@gmail.com", Objects.requireNonNull(response.getBody()).getEmail());
        Assertions.assertEquals("@bob", response.getBody().getNickname());
    }

    @Test
    public void testSignIn() {
        ResponseEntity<String> response = restTemplate.getForEntity("/signin", String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("signin", response.getBody());
    }
}
