package com.hr_handlers.chat.entity;

import com.hr_handlers.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat")
public class Chat {

    @EmbeddedId
    private ChatId id;

    @MapsId("chatRoomId")
    @ManyToOne(targetEntity=ChatRoom.class)
    private ChatRoom chatRoom;

    @MapsId("employeeId")
    @ManyToOne(targetEntity=Employee.class)
    private Employee employee;

}
