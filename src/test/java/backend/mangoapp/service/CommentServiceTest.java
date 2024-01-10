package backend.mangoapp.service;

import backend.mangoapp.entity.Comment;
import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import backend.mangoapp.repository.CommentRepository;
import backend.mangoapp.service.commentService.CommentService;
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
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    public void setup() {
        user = new User("carl@gmail.com", "12345");
        post = new Post("Test description", user, List.of());
        comment = new Comment("Test comment", user, post);
    }

    @Test
    public void saveCommentTest() {
        Comment savedComment = new Comment("Save this comment", user, post);
        commentService.add(savedComment);
        verify(commentRepository, times(1)).save(savedComment);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getUser()).isNotNull();
        assertThat(savedComment.getPost()).isNotNull();
    }

    @Test
    public void getCommentByIdTest() {
        long id = comment.getId();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        Optional<Comment> result = commentService.getById(id);
        verify(commentRepository, times(1)).findById(id);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getUser()).isNotNull();
        assertThat(result.get().getPost()).isNotNull();

        assertThat(result.get()).isEqualTo(comment);
    }

    @Test
    public void getAllCommentsTest() {
        Comment newComment = new Comment("Test comment", user, post);
        commentService.add(newComment);
        verify(commentRepository, times(1)).save(newComment);

        assertThat(newComment).isNotNull();
        assertThat(newComment.getUser()).isNotNull();
        assertThat(newComment.getPost()).isNotNull();

        List<Comment> comments = List.of(comment, newComment);
        assertThat(commentService.getAll()).isNotNull();
        assertThat(comments.size()).isEqualTo(2);
    }

    @Test
    public void deleteCommentByIdTest() {
        long id = comment.getId();

        commentService.deleteById(id);
        verify(commentRepository, times(1)).deleteById(id);
        assertThat(commentService.getById(id)).isEmpty();
    }

    @Test
    public void deleteCommentEntityTest() {
        long id = comment.getId();

        commentService.delete(comment);
        verify(commentRepository, times(1)).delete(comment);
        assertThat(commentService.getById(id)).isEmpty();
    }
}
