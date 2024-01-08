package backend.mangoapp.utils;

public final class NicknameManager {
    public static String setNickname(String email) {
        String[] nickName = email.split("@");
        return "@" + nickName[0];
    }
}
