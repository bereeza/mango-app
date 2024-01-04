package backend.mangoapp.controller;

import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import backend.mangoapp.service.postService.PostService;
import backend.mangoapp.service.userService.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HomeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    private Post post;

    @BeforeEach
    public void setup() {
        User user = new User("bob@gmail.com", "12345", "@bob");
        when(userService.add(any(User.class))).thenReturn(user);
        post = new Post("Test post", Timestamp.valueOf(LocalDateTime.now()), user, List.of());
    }

    @Test
    public void getAllPosts() {
        when(postService.getAll()).thenReturn(List.of(post));

        ParameterizedTypeReference<List<Post>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<Post>> responseEntity = restTemplate.exchange(
                "/home",
                HttpMethod.GET,
                null,
                parameterizedTypeReference
        );

        List<Post> posts = responseEntity.getBody();
        assertThat(posts).isNotNull();
        assertThat(posts).hasSize(1);
    }
}
