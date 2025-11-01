package com.posong.ai_laundry.dto;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageResponse {
    private String conversationId;
    private String assistantMessage;
}