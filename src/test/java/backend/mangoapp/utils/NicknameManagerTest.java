package backend.mangoapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NicknameManagerTest {
    private String email;

    @BeforeEach
    public void setup() {
        email = "test@gmail.com";
    }

    @Test
    public void setEmailTest() {
        String nickname = NicknameManager.setNickname(email);
        assertThat(nickname).isEqualTo("@test");
    }
}
