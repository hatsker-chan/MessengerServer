package org.example.pojo;

public class MessageDto {
    private UserDto user;
    private String messageText;

    public MessageDto(UserDto user, String messageText) {
        this.user = user;
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
