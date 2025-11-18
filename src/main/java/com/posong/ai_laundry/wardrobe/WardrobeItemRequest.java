package com.posong.ai_laundry.wardrobe;

import com.posong.ai_laundry.wardrobe.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardrobeItemRequest {

    private String name;
    private String brand;
    private Category category;
    private String color;
    private String pattern;
    private String season;
    private String thickness;
    private String material;
    private String washingMethod;
    private int usageCount;
    private String imageUrl;
}
