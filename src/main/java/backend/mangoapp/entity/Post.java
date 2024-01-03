package backend.mangoapp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Post(String description, Timestamp createdAt, User user, List<Comment> comments) {
        this.description = description;
        this.createdAt = createdAt;
        this.user = user;
        this.comments = comments;
    }

    public Post(String description, Timestamp timestamp) {
        this.description = description;
        this.createdAt = timestamp;
    }
}
