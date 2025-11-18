package com.posong.ai_laundry.service;


import com.posong.ai_laundry.dto.LaundryAdvice;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.util.MimeTypeUtils;

import java.io.IOException;



@Service
public class LaundryAiService {

    private final ChatClient chatClient;

    public LaundryAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public LaundryAdvice analyze(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        String ct = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        MimeType mime = ct.toLowerCase().contains("png") ? MimeTypeUtils.IMAGE_PNG : MimeTypeUtils.IMAGE_JPEG;

        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override public String getFilename() {
                return mime.equals(MimeTypeUtils.IMAGE_PNG) ? "image.png" : "image.jpg";
            }
        };

        // 1) system 프롬프트 수정 - 카테고리 + 소분류 강제
        String system = """
        당신은 한국어로 답하는 '의류 분석 및 세탁 전문가'입니다.
        이미지 속 의류를 보고 아래 항목을 반드시 판단하세요:

        1) 상위 카테고리(예: 상의, 아우터, 하의, 원피스·세트, 이너웨어, 트레이닝, 모자, 스카프·머플러, 양말, 장갑, 가방, 침구류)
        2) 소분류(예: 니트, 가디건, 셔츠, 코트, 패딩, 후드티, 트레이닝팬츠, 슬랙스 등)
        3) 소재 / 색상 / 세탁방법 / 주의사항 / 권장심볼

        **절대 '확실하지 않음', '추정' 같은 표현을 하지 말고 하나로 단정하세요.**
        실루엣, 소재 질감, 재봉선 형태, 소매/네크라인 구조 등 시각적 근거로 판단하십시오.
    """;

        // 2) JSON 스키마 적용
        String user = """
        아래 JSON 스키마에 맞게 **정확한 JSON만** 생성하세요.

        {
          "카테고리": "string",
          "소분류": "string",
          "소재": "string",
          "색상": "string",
          "세탁방법": "string",
          "주의사항": "string",
          "권장심볼": ["string"]
        }
    """;

        return chatClient.prompt()
                .system(system)
                .user(u -> u.text(user).media(mime, resource))
                .call()
                .entity(LaundryAdvice.class);
    }
}