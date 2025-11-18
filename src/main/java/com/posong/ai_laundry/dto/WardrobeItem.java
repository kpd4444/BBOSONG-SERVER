package com.posong.ai_laundry.dto;

import com.posong.ai_laundry.wardrobe.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WardrobeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String brand;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String color;

    private String pattern;

    private String season;

    private String thickness;

    private String material;

    private String washingMethod;

    private int usageCount;

    private String imageUrl;

    @Builder
    public WardrobeItem(
            Long id,
            String name,
            String brand,
            Category category,
            String color,
            String pattern,
            String season,
            String thickness,
            String material,
            String washingMethod,
            int usageCount,
            String imageUrl
    ) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.color = color;
        this.pattern = pattern;
        this.season = season;
        this.thickness = thickness;
        this.material = material;
        this.washingMethod = washingMethod;
        this.usageCount = usageCount;
        this.imageUrl = imageUrl;
    }
}
