package com.posong.ai_laundry.controller;


import com.posong.ai_laundry.dto.LaundryAdvice;
import com.posong.ai_laundry.service.LaundryAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analyze")
@Validated
public class LaundryController {

    private final LaundryAiService laundryAiService;

    @PostMapping(value="/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LaundryAdvice> analyze(@RequestPart("file") MultipartFile file) throws Exception {
        LaundryAdvice advice = laundryAiService.analyze(file);
        return ResponseEntity.ok(advice);
    }

    // 헬스체크/스모크
    @GetMapping("/ping")
    public String ping() { return "pong"; }
}