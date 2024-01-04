package backend.mangoapp.controller;

import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import backend.mangoapp.service.postService.PostService;
import backend.mangoapp.service.userService.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User("bob@gmail.com", "12345", "@bob");
        userService.add(user);
        Post firstUserPost = new Post("First user post",
                Timestamp.valueOf(LocalDateTime.now()),
                user,
                List.of());

        postService.add(firstUserPost);
    }

    @Test
    public void getAllPersonalPostsTest() {
        long userId = user.getId();
        ResponseEntity<List<Post>> response = restTemplate.exchange(
                "/u/" + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        List<Post> personalPosts = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(personalPosts.size()).isEqualTo(1);
        assertThat(personalPosts.get(0).getUser().getId()).isEqualTo(userId);
        assertThat(personalPosts.get(0).getDescription()).isEqualTo("First user post");
    }

    @Test
    public void addPostTest() {
        Post newPost = new Post("New post",
                Timestamp.valueOf(LocalDateTime.now()),
                user,
                List.of());

        ResponseEntity<Post> response = restTemplate.postForEntity("/u/{id}",
                newPost,
                Post.class,
                user.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getDescription()).isEqualTo("New post");
    }

    @Test
    public void deletePostTest() {
        Post postToDelete = postService.getAll().get(0);

        ResponseEntity<String> response = restTemplate.exchange("/u/{id}/post/{postId}",
                HttpMethod.DELETE,
                null,
                String.class, user.getId(), postToDelete.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Post deleted");
    }
}
