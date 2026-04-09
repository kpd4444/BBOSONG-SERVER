package com.posong.ai_laundry.chat;

import com.posong.ai_laundry.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_chat_room_conversation_id", columnList = "conversation_id", unique = true),
        @Index(name = "idx_chat_room_last_message_at", columnList = "last_message_at")
})
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false, unique = true, length = 36)
    private String conversationId;

    @Column(name = "last_message_at", nullable = false)
    private LocalDateTime lastMessageAt;
}
