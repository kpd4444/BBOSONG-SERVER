package com.posong.ai_laundry.service;

import com.posong.ai_laundry.chat.ConversationStore; // ← 네 위치에 맞게 조정
import com.posong.ai_laundry.dto.ChatMessageResponse; // ← 네 위치에 맞게 조정
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Service
public class ChatService {

    private final ConversationStore store;
    private final ChatClient chatClient;

    public ChatService(ConversationStore store, ChatClient.Builder builder) {
        this.store = store;
        this.chatClient = builder.build(); // ✅ Builder로 실제 클라이언트 생성
    }

    private static final String SYSTEM = """
        당신은 한국어로 답하는 '뽀송이 세탁 전문가'입니다.
        원단/염색/오염(얼룩) 상황에 맞게 안전한 단계별 세탁법을 간결한 불릿으로 안내하세요.
        가능하면 온도(°C), 세제(중성/산소계/염소계), 코스(울/니트/표준), 건조/다림질을 포함하고
        색 빠짐/수축/이염 등 위험 요소는 강조 표시하세요.
        이전 대화와 첨부 이미지를 모두 문맥에 반영합니다.
        """;

    public ChatMessageResponse sendMessage(String conversationId, String userText, MultipartFile file) {
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = store.start();
        }

        // 간단한 히스토리 문자열
        List<String> prev = store.get(conversationId);
        StringBuilder history = new StringBuilder();
        for (String line : prev) history.append(line).append("\n");

        store.append(conversationId, "user", userText == null ? "" : userText);

        var options = OpenAiChatOptions.builder()
                .model("gpt-5")     // ✅ 모델 지정
                .temperature(1.0)   // ✅ gpt-5는 1만 허용
                .build();

        var call = chatClient.prompt()
                .options(OpenAiChatOptions.builder()
                        .model("gpt-5")
                        .temperature(1.0)
                        .build())
                .system(SYSTEM)
                .user(u -> {
                    u.text("이전 대화:\n" + history + "\n---\n사용자 메시지: " + (userText == null ? "" : userText));
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
        store.append(conversationId, "assistant", assistant);

        return ChatMessageResponse.builder()
                .conversationId(conversationId)
                .assistantMessage(assistant)
                .build();
    }

    public String startConversation() {
        return store.start();
    }
}