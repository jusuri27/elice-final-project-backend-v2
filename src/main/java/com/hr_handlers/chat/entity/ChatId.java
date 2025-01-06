package com.hr_handlers.chat.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatId implements Serializable {
    @JoinColumn(name = "chat_room_id")
    private Long chatRoomId;

    @JoinColumn(name = "employee_id")
    private Long employeeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatId chatId = (ChatId) o;
        return chatRoomId.equals(chatId.chatRoomId) && employeeId.equals(chatId.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId, employeeId);
    }
}
