package com.posong.ai_laundry.wardrobe;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final String uploadPath = "C:/upload/images/";

    public String saveImage(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadPath + fileName);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        // 클라이언트에서 접근 가능한 URL로 변경해야 함
        return "/images/" + fileName;
    }
}
