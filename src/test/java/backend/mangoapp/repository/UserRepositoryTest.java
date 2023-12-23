package backend.mangoapp.repository;

import backend.mangoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    private void setup() {
        user = new User("carl@gmail.com", "12345", "@carl");
    }

    @Test
    @DisplayName("Saved user is not null and id is greater than 0")
    public void saveUserTest() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Get user by id")
    public void getUserById() {
        User userById = userRepository.getReferenceById(user.getId());
        assertThat(userById).isNotNull();
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsersTest() {
        User newUser = new User("bob@gmail.com", "12345", "@bob");
        userRepository.save(newUser);
        List<User> users = List.of(user, newUser);
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Delete user by id")
    public void deleteUserByIdTest() {
        userRepository.delete(user);
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}
