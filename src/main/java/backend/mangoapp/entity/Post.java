package backend.mangoapp.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Post(String description, Timestamp createdAt, User user, List<Comment> comments) {
        this(description, createdAt);
        this.user = user;
        this.comments = comments;
    }

    public Post(String description, Timestamp createdAt) {
        this.description = description;
        this.createdAt = createdAt;
    }
}
