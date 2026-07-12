package com.cafex.pos.controller;

import com.cafex.pos.dto.RecipePageResponse;
import com.cafex.pos.dto.RecipeProductionResponse;
import com.cafex.pos.dto.RecipeRequest;
import com.cafex.pos.dto.RecipeResponse;
import com.cafex.pos.entity.User;
import com.cafex.pos.repository.UserRepository;
import com.cafex.pos.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class RecipeController {

    private final RecipeService recipeService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest request) {
        log.info("Create recipe request received: {}", request.getName());
        RecipeResponse response = recipeService.createRecipe(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeRequest request) {
        log.info("Update recipe request received for ID: {}", id);
        RecipeResponse response = recipeService.updateRecipe(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id) {
        log.info("Get recipe by ID: {}", id);
        Optional<RecipeResponse> response = recipeService.getRecipeById(id);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<RecipePageResponse> getRecipesByRestaurant(
            @RequestParam("restaurant_id") Long restaurantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "name", required = false) String name) {
        log.info("Get recipes for restaurant: {} with page: {}, size: {}, name: {}", restaurantId, page, size, name);
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        RecipePageResponse response = recipeService.getRecipesByRestaurant(restaurantId, pageable, name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecipeResponse>> getAllRecipesByRestaurant(
            @RequestParam("restaurant_id") Long restaurantId) {
        log.info("Get all recipes for restaurant: {}", restaurantId);
        List<RecipeResponse> response = recipeService.getRecipesByRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        log.info("Delete recipe request received for ID: {}", id);
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/produce")
    public ResponseEntity<Void> produceRecipe(@RequestBody Map<String, Object> request) {
        log.info("Produce recipe request received: {}", request);

        Long menuItemId = ((Number) request.get("menu_item_id")).longValue();
        Double batchMultiplier = ((Number) request.get("batch_multiplier")).doubleValue();
        String note = (String) request.get("note");

        Long createdBy = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            Optional<User> userOpt = userRepository.findByUsername(auth.getName());
            createdBy = userOpt.map(User::getId).orElse(null);
        }

        recipeService.produceRecipe(menuItemId, batchMultiplier, note, createdBy);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/productions")
    public ResponseEntity<Map<String, Object>> getRecipeProductions(
            @RequestParam("restaurant_id") Long restaurantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "name", required = false) String name) {
        log.info("Get recipe productions for restaurant: {} with page: {}, size: {}, name: {}", restaurantId, page, size, name);
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Map<String, Object> response = recipeService.getRecipeProductionsByRestaurant(restaurantId, pageable, name);
        return ResponseEntity.ok(response);
    }
}
