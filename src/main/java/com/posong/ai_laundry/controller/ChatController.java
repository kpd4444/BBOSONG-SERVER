package com.posong.ai_laundry.controller;

import com.posong.ai_laundry.dto.ChatMessageResponse;
import com.posong.ai_laundry.dto.ChatStartResponse;
import com.posong.ai_laundry.global.ApiResponse;
import com.posong.ai_laundry.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<ChatStartResponse>> start() {
        return ResponseEntity.ok(ApiResponse.success(new ChatStartResponse(chatService.startConversation())));
    }

    @PostMapping(
            value = "/send",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ChatMessageResponse>> send(
            @RequestPart(value = "conversationId", required = false) String conversationId,
            @RequestPart(value = "text", required = false) String text,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(ApiResponse.success(chatService.sendMessage(conversationId, text, file)));
    }
}