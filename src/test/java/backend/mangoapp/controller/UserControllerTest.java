package backend.mangoapp.controller;

import backend.mangoapp.service.postService.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostService postService;

    @Test
    public void getAllPersonalPostsTest() {
        System.out.println();
    }
}
