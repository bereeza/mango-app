package backend.mangoapp.controller;

import backend.mangoapp.entity.User;
import org.junit.jupiter.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testSignUp() {
        User user = new User("bob@gmail.com", "12345", "@bob");
        ResponseEntity<User> response = restTemplate.postForEntity("/", user, User.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertThat(Objects.requireNonNull(response.getBody()).getId()).isGreaterThan(0);
        Assertions.assertEquals("bob@gmail.com", response.getBody().getEmail());
        Assertions.assertEquals("@bob", response.getBody().getNickname());
    }

    @Test
    public void testSignIn() {
        ResponseEntity<String> response = restTemplate.getForEntity("/signin", String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("signin", response.getBody());
    }
}
