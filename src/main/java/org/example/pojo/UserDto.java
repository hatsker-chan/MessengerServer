package org.example.pojo;

public class UserDto {
    private int user_id;
    private String nickname;

    public UserDto(int userId, String nickname) {
        user_id = userId;
        this.nickname = nickname;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
