package backend.mangoapp.repository;

import backend.mangoapp.entity.Comment;
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
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    public void setup() {
        user = new User("carl@gmail.com", "12345");
        post = new Post("test description");
        comment = new Comment("Test comment", user, post);
    }

    @Test
    public void saveCommentTest() {
        Comment savedComment = commentRepository.save(comment);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getUser()).isNotNull();
        assertThat(savedComment.getPost()).isNotNull();
        assertThat(savedComment.getId()).isGreaterThan(0);
    }

    @Test
    public void getCommentByIdTest() {
        long id = comment.getId();
        Comment commentById = commentRepository.getReferenceById(id);
        assertThat(commentById).isNotNull();
    }

    @Test
    public void getAllCommentsTest() {
        Comment newComment = new Comment("Second comment",  user, post);
        commentRepository.save(newComment);

        assertThat(newComment.getUser()).isNotNull();
        assertThat(newComment.getPost()).isNotNull();

        List<Comment> comments = List.of(comment, newComment);
        assertThat(comments.size()).isEqualTo(2);
    }

    @Test
    public void deleteCommentByIdTest() {
        long id = comment.getId();
        commentRepository.delete(comment);
        assertThat(commentRepository.findById(id)).isEmpty();
    }
}
