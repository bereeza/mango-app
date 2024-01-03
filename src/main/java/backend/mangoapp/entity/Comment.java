package backend.mangoapp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    private Timestamp created_at;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Comment(String comment, Timestamp created_at, User user, Post post) {
        this.comment = comment;
        this.created_at = created_at;
        this.user = user;
        this.post = post;
    }

    public Comment(String comment, Timestamp created_at) {
        this.comment = comment;
        this.created_at = created_at;
    }
}
