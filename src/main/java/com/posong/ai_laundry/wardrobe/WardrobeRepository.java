package com.posong.ai_laundry.wardrobe;

import com.posong.ai_laundry.dto.WardrobeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardrobeRepository extends JpaRepository<WardrobeItem, Long> {

    List<WardrobeItem> findByCategory(Category category);

    List<WardrobeItem> findByColor(String color);

    List<WardrobeItem> findByBrand(String brand);
}
