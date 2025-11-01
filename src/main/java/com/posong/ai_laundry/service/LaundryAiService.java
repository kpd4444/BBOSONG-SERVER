package com.posong.ai_laundry.service;


import com.posong.ai_laundry.dto.LaundryAdvice;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.io.UncheckedIOException;



@Service
public class LaundryAiService {

    private final ChatClient chatClient;

    public LaundryAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public LaundryAdvice analyze(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        // 업로드된 실제 contentType 사용(없으면 JPEG 기본)
        String ct = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        MimeType mime = ct.toLowerCase().contains("png") ? MimeTypeUtils.IMAGE_PNG : MimeTypeUtils.IMAGE_JPEG;

        // 파일명을 갖는 ByteArrayResource (일부 모델이 파일명 요구할 때 대비)
        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override public String getFilename() { return mime.equals(MimeTypeUtils.IMAGE_PNG) ? "image.png" : "image.jpg"; }
        };

        String system = """
            당신은 한국어로 답하는 '의류 세탁 전문가'입니다.
            이미지 속 의류를 보고 소재/색상/세탁방법/주의사항을 정확하고 간결하게 정리하세요.
            세탁방법에는 세제 종류, 최대 온도(°C), 코스(손세탁/드럼/울/니트), 건조/다림질을 포함하세요.
            '권장심볼'은 가능하면 국제 세탁 심볼에 대응하는 간단한 코드 목록으로 제시하세요.
        """;

        String user = """
            아래 JSON 스키마에 맞게 **정확한 JSON만** 생성하세요.
            {
              "소재": "string",
              "색상": "string",
              "세탁방법": "string",
              "주의사항": "string",
              "권장심볼": ["string"]
            }
        """;

        return chatClient.prompt()
                .system(system)
                .user(u -> u
                        .text(user)
                        .media(mime, resource)
                )
                .call()
                .entity(LaundryAdvice.class);
    }
}