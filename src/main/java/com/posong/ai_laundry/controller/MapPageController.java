package com.posong.ai_laundry.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapPageController {
    @Value("${kakao.js-key}")
    private String kakaoJsKey;

    @GetMapping("/map.html")
    public String map(Model model) {
        model.addAttribute("KAKAO_JS_KEY", kakaoJsKey);
        return "map";  // templates/map.html
    }
}


