package backend.mangoapp.service;

import backend.mangoapp.entity.User;
import backend.mangoapp.repository.UserRepository;
import backend.mangoapp.service.userService.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User("carl@gmail.com", "12345", "@carl");
    }

    @Test
    @DisplayName("Saved user is not null")
    public void saveUserTest() {
        User savedUser = new User("bob@gmail.com", "12345", "@bob");
        userService.add(savedUser);
        verify(userRepository, times(1)).save(savedUser);
        assertThat(savedUser).isNotNull();
    }

    @Test
    @DisplayName("Get user by id")
    public void getUserByIdTest() {
        long id = user.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getById(id);
        verify(userRepository, times(1)).findById(id);
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsersTest() {
        User newUser = new User("bob@gmail.com", "12345", "@bob");
        userService.add(newUser);
        verify(userRepository, times(1)).save(newUser);
        assertThat(newUser).isNotNull();

        List<User> users = List.of(user, newUser);
        assertThat(userService.getAll()).isNotNull();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Delete user by id")
    public void deleteUserByIdTest() {
        long id = user.getId();

        userService.deleteById(id);
        verify(userRepository, times(1)).deleteById(id);
        assertThat(userService.getById(id)).isEmpty();
    }

    @Test
    @DisplayName("Delete user entity")
    public void deleteUserEntityTest() {
        userService.delete(user);
        verify(userRepository, times(1)).delete(user);
        assertThat(userService.getById(user.getId())).isEmpty();
    }
}
