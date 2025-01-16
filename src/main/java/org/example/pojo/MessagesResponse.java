package org.example.pojo;

import java.util.List;

public class MessagesResponse {
    private List<MessageDto> messages;

    public MessagesResponse(List<MessageDto> messages) {

        this.messages = messages;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }
}
