package org.example.pojo;

import org.example.entities.Message;
import org.example.entities.User;

import java.util.List;

public class Mapper {
    public List<MessageDto> mapListMessageToDto(List<Message> messages) {
        return messages.stream().map((Message message) -> {
            User user = message.sender();
            return new MessageDto(
                    new UserDto(
                            user.id(),
                            user.name()
                    ),
                    message.text()
            );
        }).toList();
    }
}
