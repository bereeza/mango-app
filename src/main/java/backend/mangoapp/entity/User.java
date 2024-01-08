package backend.mangoapp.entity;

import backend.mangoapp.utils.NicknameManager;
import backend.mangoapp.utils.PasswordManger;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @JsonCreator
    public User(@JsonProperty("email") String email,
                @JsonProperty("password") String password) {
        this.email = email;
        this.password = PasswordManger.hashPassword(password);
        this.nickname = NicknameManager.setNickname(email);
    }
}
