package com.cafex.pos.repository;

import com.cafex.pos.entity.MenuCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RestaurantMenuCategoriesRepository extends JpaRepository<MenuCategories, Long>, JpaSpecificationExecutor<MenuCategories> {
    boolean existsByKeyAndRestaurantId(String key, Long restaurantId);
    boolean existsByNameAndRestaurantId(String name, Long restaurantId);
    boolean existsByKeyAndRestaurantIdAndIdNot(String key, Long restaurantId, Long id);
    boolean existsByNameAndRestaurantIdAndIdNot(String name, Long restaurantId, Long id);
    List<MenuCategories> findByRestaurantIdAndIsActiveOrderByDisplayOrderAsc(Long restaurantId, Boolean isActive);
    List<MenuCategories> findByRestaurantIdOrderByDisplayOrderAsc(Long restaurantId);
    Optional<MenuCategories> findByRestaurantIdAndId(Long restaurantId, Long id);
    long countByRestaurantIdAndIsActive(Long restaurantId, Boolean isActive);

    @Modifying
    @Transactional
    @Query("UPDATE MenuCategories mc SET mc.itemCount = :count WHERE mc.restaurant.id = :restaurantId AND mc.key = :categoryKey")
    int updateItemCountByRestaurantAndCategory(@Param("restaurantId") Long restaurantId, @Param("categoryKey") String categoryKey, @Param("count") Integer count);
}
