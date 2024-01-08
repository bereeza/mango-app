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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    private User user;
    private Post post;

    @BeforeEach
    public void setup() {
        user = new User("mike@gmail.com", "12345");
        post = new Post("First user post",
                Timestamp.valueOf(LocalDateTime.now()),
                user,
                List.of());

    }

    @Test
    public void getAllPersonalPostsTest() {
        restTemplate = restTemplate.withBasicAuth("mike@gmail.com", "12345");
        long userId = user.getId();

        when(userService.getById(userId)).thenReturn(Optional.of(user));
        when(postService.getAll()).thenReturn(List.of(post));

        ResponseEntity<List<Post>> response = restTemplate.exchange(
                "/u/" + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        List<Post> personalPosts = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(personalPosts.size()).isEqualTo(1);
        assertThat(personalPosts.get(0).getUser().getId()).isEqualTo(userId);
        assertThat(personalPosts.get(0).getDescription()).isEqualTo("First user post");
    }

    @Test
    public void addPostTest() {
        restTemplate = restTemplate.withBasicAuth("mike@gmail.com", "12345");
        when(userService.getById(user.getId())).thenReturn(Optional.of(user));

        Post newPost = new Post("New post",
                Timestamp.valueOf(LocalDateTime.now()),
                user,
                List.of());

        when(postService.add(any(Post.class))).thenReturn(newPost);

        ResponseEntity<Post> response = restTemplate.postForEntity("/u/{id}",
                newPost,
                Post.class,
                user.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getDescription()).isEqualTo("New post");
    }
}
