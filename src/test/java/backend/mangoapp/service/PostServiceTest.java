package backend.mangoapp.service;

import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import backend.mangoapp.repository.PostRepository;
import backend.mangoapp.service.postService.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private Post post;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User("carl@gmail.com", "12345");
        post = new Post("Test description", user, List.of());
    }

    @Test
    public void savePostTest() {
        Post savedPost = new Post("Save this post",  user, List.of());
        postService.add(savedPost);
        verify(postRepository, times(1)).save(savedPost);
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getUser()).isNotNull();
    }

    @Test
    public void getPostByIdTest() {
        long id = post.getId();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        Optional<Post> result = postService.getById(id);
        verify(postRepository, times(1)).findById(id);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getUser()).isNotNull();
        assertThat(result.get()).isEqualTo(post);
    }

    @Test
    public void getAllPostsTest() {
        Post newPost = new Post("New post!", user, List.of());
        postService.add(newPost);
        verify(postRepository, times(1)).save(newPost);
        assertThat(newPost).isNotNull();
        assertThat(newPost.getUser()).isNotNull();

        List<Post> posts = List.of(post, newPost);
        assertThat(postService.getAll()).isNotNull();
        assertThat(posts.size()).isEqualTo(2);
    }

    @Test
    public void deletePostByIdTest() {
        long id = post.getId();

        postService.deleteById(id);
        verify(postRepository, times(1)).deleteById(id);
        assertThat(postService.getById(id)).isEmpty();
    }

    @Test
    public void deletePostEntityTest() {
        long id = post.getId();
        postService.delete(post);
        verify(postRepository, times(1)).delete(post);
        assertThat(postService.getById(id)).isEmpty();
    }
}
