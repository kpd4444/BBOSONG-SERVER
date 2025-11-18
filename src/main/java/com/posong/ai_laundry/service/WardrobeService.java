package com.posong.ai_laundry.service;

import com.posong.ai_laundry.dto.WardrobeSaveRequest;
import com.posong.ai_laundry.dto.WardrobeItem;
import com.posong.ai_laundry.wardrobe.WardrobeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WardrobeService {

    private final WardrobeRepository repository;

    public WardrobeItem save(WardrobeSaveRequest req) {
        WardrobeItem item = WardrobeItem.builder()
                .name(req.getName())
                .brand(req.getBrand())
                .category(req.getCategory())
                .color(req.getColor())
                .pattern(req.getPattern())
                .season(req.getSeason())
                .thickness(req.getThickness())
                .material(req.getMaterial())
                .washingMethod(req.getWashingMethod())
                .usageCount(req.getUsageCount())
                .imageUrl(req.getImageUrl())
                .build();

        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
