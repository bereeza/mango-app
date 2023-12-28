package backend.mangoapp.controller;

import backend.mangoapp.entity.User;
import backend.mangoapp.service.userService.UserService;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

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
    public void testSignIn_UserFoundAndCorrectPassword() {
        when(userService.findUserByEmail("bob@gmail.com")).thenReturn(Optional.of(user));

        ResponseEntity<String> response = restTemplate.getForEntity("/signin?email=bob@gmail.com&password=12345", String.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("User signed in successfully", response.getBody());
    }

    @Test
    public void testSignIn_UserFoundButIncorrectPassword() {
        Mockito.when(userService.findUserByEmail("bob@gmail.com")).thenReturn(Optional.of(user));

        ResponseEntity<String> response = restTemplate.getForEntity("/signin?email=bob@gmail.com&password=wrongpassword", String.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals("Incorrect password", response.getBody());
    }

    @Test
    public void testSignIn_UserNotFound() {
        Mockito.when(userService.findUserByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.getForEntity("/signin?email=unknown@gmail.com&password=12345", String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("User not found", response.getBody());
    }
}
