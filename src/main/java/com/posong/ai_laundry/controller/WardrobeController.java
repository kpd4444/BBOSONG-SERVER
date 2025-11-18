package com.posong.ai_laundry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posong.ai_laundry.dto.WardrobeItem;
import com.posong.ai_laundry.wardrobe.Category;
import com.posong.ai_laundry.wardrobe.ImageStorageService;
import com.posong.ai_laundry.wardrobe.WardrobeRepository;
import com.posong.ai_laundry.wardrobe.WardrobeItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/wardrobe")
@RequiredArgsConstructor
public class WardrobeController {

    private final WardrobeRepository wardrobeRepository;
    private final ImageStorageService imageStorageService;

    @PostMapping(value = "/add-with-image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public WardrobeItem addItemWithImage(
            @RequestPart(value = "data", required = true) String jsonString,
            @RequestPart("image") MultipartFile image
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        WardrobeItemRequest request = mapper.readValue(jsonString, WardrobeItemRequest.class);

        String imageUrl = imageStorageService.saveImage(image);

        WardrobeItem item = WardrobeItem.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .category(request.getCategory())
                .color(request.getColor())
                .pattern(request.getPattern())
                .season(request.getSeason())
                .thickness(request.getThickness())
                .material(request.getMaterial())
                .washingMethod(request.getWashingMethod())
                .usageCount(request.getUsageCount())
                .imageUrl(imageUrl)
                .build();
        return wardrobeRepository.save(item);
    }



    // 전체 리스트 조회
    @GetMapping("/list")
    public List<WardrobeItem> list() {
        return wardrobeRepository.findAll();
    }

    // 단일 조회
    @GetMapping("/{id}")
    public WardrobeItem getItem(@PathVariable Long id) {
        return wardrobeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 아이템이 존재하지 않습니다: " + id));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id) {
        wardrobeRepository.deleteById(id);
        return "Deleted ID = " + id;
    }

    // 카테고리 별 검색 : 카테고리는 ENUM 확인
    @GetMapping("/category/{category}")
    public List<WardrobeItem> getByCategory(@PathVariable Category category) {
        return wardrobeRepository.findByCategory(category);
    }

    // 색상 별
    @GetMapping("/color/{color}")
    public List<WardrobeItem> getByColor(@PathVariable String color) {
        return wardrobeRepository.findByColor(color);
    }

    // 브랜드 별
    @GetMapping("/brand/{brand}")
    public List<WardrobeItem> getByBrand(@PathVariable String brand) {
        return wardrobeRepository.findByBrand(brand);
    }
}
