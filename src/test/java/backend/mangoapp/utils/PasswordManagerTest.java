package backend.mangoapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordManagerTest {

    private String password;

    @BeforeEach
    public void setup() {
        password = "12345";
    }

    @Test
    public void hashPasswordTest() {
        String hashedPassword = PasswordManger.hashPassword(password);
        boolean ifPasswordMatches = new BCryptPasswordEncoder().matches(password, hashedPassword);
        assertThat(ifPasswordMatches).isTrue();
    }
}
