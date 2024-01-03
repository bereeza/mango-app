package backend.mangoapp.repository;

import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User user;
    private Post post;

    @BeforeEach
    public void setup() {
        user = new User("carl@gmail.com", "12345", "@carl");
        user = userRepository.save(user);
        post = new Post("Test post", Timestamp.valueOf(LocalDateTime.now()), user, List.of());
    }

    @Test
    public void savePostTest() {
        Post savedPost = postRepository.save(post);
        assertThat(savedPost.getUser()).isNotNull();
        assertThat(savedPost.getId()).isGreaterThan(0);
    }

    @Test
    public void getPostByIdTest() {
        long id = post.getId();
        Post postById = postRepository.getReferenceById(id);
        assertThat(postById).isNotNull();
    }

    @Test
    public void getAllPostsTest() {
        Post newPost = new Post("This is my second post", Timestamp.valueOf(LocalDateTime.now()), user, List.of());

        postRepository.save(newPost);

        assertThat(newPost.getUser()).isNotNull();

        List<Post> posts = List.of(post, newPost);
        assertThat(posts.size()).isEqualTo(2);
    }

    @Test
    public void deletePostByIdTest() {
        long id = post.getId();
        postRepository.delete(post);
        assertThat(postRepository.findById(id)).isEmpty();
    }
}
