package com.posong.ai_laundry.service;

import com.posong.ai_laundry.chat.ChatMessage;
import com.posong.ai_laundry.chat.ChatMessageRepository;
import com.posong.ai_laundry.chat.ChatRoom;
import com.posong.ai_laundry.chat.ChatRoomRepository;
import com.posong.ai_laundry.config.AiProperties;
import com.posong.ai_laundry.config.ChatProperties;
import com.posong.ai_laundry.dto.ChatMessageResponse;
import com.posong.ai_laundry.global.CustomException;
import com.posong.ai_laundry.global.ErrorCode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiProperties aiProperties;
    private final ChatProperties chatProperties;

    public ChatService(
            ChatClient.Builder builder,
            ChatRoomRepository chatRoomRepository,
            ChatMessageRepository chatMessageRepository,
            AiProperties aiProperties,
            ChatProperties chatProperties
    ) {
        this.chatClient = builder.build(); // ✅ Builder로 실제 클라이언트 생성
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.aiProperties = aiProperties;
        this.chatProperties = chatProperties;
    }

    private static final String SYSTEM = """
        당신은 한국어로 답하는 '뽀송이 세탁 전문가'입니다.
        원단/염색/오염(얼룩) 상황에 맞게 안전한 단계별 세탁법을 간결한 불릿으로 안내하세요.
        가능하면 온도(°C), 세제(중성/산소계/염소계), 코스(울/니트/표준), 건조/다림질을 포함하고
        색 빠짐/수축/이염 등 위험 요소는 강조 표시하세요.
        이전 대화와 첨부 이미지를 모두 문맥에 반영합니다.
        """;

    @Transactional
    public ChatMessageResponse sendMessage(String conversationId, String userText, MultipartFile file) {
        ChatRoom chatRoom = resolveChatRoom(conversationId);
        String normalizedUserText = userText == null ? "" : userText;

        String history = buildHistory(chatRoom);
        saveMessage(chatRoom, "user", normalizedUserText, file != null && !file.isEmpty());

        var options = OpenAiChatOptions.builder()
                .model(aiProperties.model())
                .temperature(aiProperties.temperature())
                .build();

        var call = chatClient.prompt()
                .options(options)
                .system(SYSTEM)
                .user(u -> {
                    u.text("이전 대화:\n" + history + "\n---\n사용자 메시지: " + normalizedUserText);
                    if (file != null && !file.isEmpty()) {
                        try {
                            String ct = (file.getContentType() != null) ? file.getContentType() : "image/jpeg";
                            MimeType mime = ct.toLowerCase().contains("png") ? MimeTypeUtils.IMAGE_PNG : MimeTypeUtils.IMAGE_JPEG;

                            ByteArrayResource res = new ByteArrayResource(file.getBytes()) {
                                @Override public String getFilename() {
                                    return mime.equals(MimeTypeUtils.IMAGE_PNG) ? "image.png" : "image.jpg";
                                }
                            };
                            u.media(mime, res);
                        } catch (IOException e) {
                            throw new UncheckedIOException("업로드 파일 읽기 실패", e);
                        }
                    }
                    // ❌ 여기서 아무 것도 반환하지 마세요 (return 금지)
                })
                .call();

        String assistant = call.content();
        saveMessage(chatRoom, "assistant", assistant, false);

        return ChatMessageResponse.builder()
                .conversationId(chatRoom.getConversationId())
                .assistantMessage(assistant)
                .build();
    }

    @Transactional
    public String startConversation() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setConversationId(UUID.randomUUID().toString());
        chatRoom.setLastMessageAt(LocalDateTime.now());
        return chatRoomRepository.save(chatRoom).getConversationId();
    }

    private ChatRoom resolveChatRoom(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setConversationId(UUID.randomUUID().toString());
            chatRoom.setLastMessageAt(LocalDateTime.now());
            return chatRoomRepository.save(chatRoom);
        }

        return chatRoomRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_CONVERSATION_NOT_FOUND));
    }

    private String buildHistory(ChatRoom chatRoom) {
        List<ChatMessage> recentMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(
                chatRoom,
                PageRequest.of(0, chatProperties.historyLimit())
        );
        Collections.reverse(recentMessages);

        StringBuilder history = new StringBuilder();
        for (ChatMessage message : recentMessages) {
            history.append(message.getRole())
                    .append(":")
                    .append(message.getContent())
                    .append("\n");
        }
        return history.toString();
    }

    private void saveMessage(ChatRoom chatRoom, String role, String content, boolean hasAttachment) {
        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setRole(role);
        message.setContent(content);
        message.setHasAttachment(hasAttachment);
        chatMessageRepository.save(message);
        enforceRetention(chatRoom);

        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
    }

    private void enforceRetention(ChatRoom chatRoom) {
        long retentionLimit = chatProperties.retentionLimit();
        long totalMessages = chatMessageRepository.countByChatRoom(chatRoom);
        if (totalMessages > retentionLimit) {
            chatMessageRepository.deleteMessagesExceedingRetention(chatRoom.getId(), retentionLimit);
        }
    }
}
