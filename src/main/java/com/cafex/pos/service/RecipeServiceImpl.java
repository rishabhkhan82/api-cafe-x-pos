package com.cafex.pos.service;

import com.cafex.pos.dto.RecipeIngredientRequest;
import com.cafex.pos.dto.RecipeIngredientResponse;
import com.cafex.pos.dto.RecipePageResponse;
import com.cafex.pos.dto.RecipeProductionResponse;
import com.cafex.pos.dto.RecipeRequest;
import com.cafex.pos.dto.RecipeResponse;
import com.cafex.pos.entity.InventoryItem;
import com.cafex.pos.entity.InventoryStockLog;
import com.cafex.pos.entity.Recipe;
import com.cafex.pos.entity.RecipeIngredient;
import com.cafex.pos.entity.RecipeProduction;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.InventoryItemRepository;
import com.cafex.pos.repository.InventoryStockLogRepository;
import com.cafex.pos.repository.RecipeIngredientRepository;
import com.cafex.pos.repository.RecipeProductionRepository;
import com.cafex.pos.repository.RecipeRepository;
import com.cafex.pos.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RestaurantRepository restaurantRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStockLogRepository inventoryStockLogRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeProductionRepository recipeProductionRepository;

    @Override
    public RecipeResponse createRecipe(RecipeRequest request) {
        log.info("Creating new recipe: {}", request.getName());

        if (request.getRecipeId() != null && recipeRepository.existsByRecipeId(request.getRecipeId())) {
            throw new ConflictException("Recipe ID already exists: " + request.getRecipeId());
        }

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Recipe recipe = new Recipe();
        recipe.setRecipeId(request.getRecipeId());
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setServingSize(request.getServingSize());
        recipe.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        recipe.setCookingTimeMinutes(request.getCookingTimeMinutes());
        recipe.setDifficultyLevel(Recipe.DifficultyLevel.valueOf(request.getDifficultyLevel()));
        recipe.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        recipe.setMenuItemId(request.getMenuItemId());
        recipe.setRestaurant(restaurant);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());

        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            List<RecipeIngredient> ingredients = buildIngredients(request.getIngredients(), recipe);
            recipe.setIngredients(ingredients);
        }

        Recipe savedRecipe = recipeRepository.save(recipe);
        log.info("Recipe created with ID: {}", savedRecipe.getId());

        return convertToResponse(savedRecipe);
    }

    @Override
    public RecipeResponse updateRecipe(Long id, RecipeRequest request) {
        log.info("Updating recipe with ID: {}", id);

        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with ID: " + id));

        existingRecipe.setName(request.getName());
        existingRecipe.setDescription(request.getDescription());
        existingRecipe.setServingSize(request.getServingSize());
        existingRecipe.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        existingRecipe.setCookingTimeMinutes(request.getCookingTimeMinutes());

        if (request.getDifficultyLevel() != null && !request.getDifficultyLevel().isBlank()) {
            existingRecipe.setDifficultyLevel(Recipe.DifficultyLevel.valueOf(request.getDifficultyLevel().trim().toUpperCase()));
        }

        existingRecipe.setIsActive(request.getIsActive());
        existingRecipe.setMenuItemId(request.getMenuItemId());
        existingRecipe.setUpdatedAt(LocalDateTime.now());

        existingRecipe.getIngredients().clear();
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            List<RecipeIngredient> ingredients = buildIngredients(request.getIngredients(), existingRecipe);
            ingredients.forEach(ing -> existingRecipe.getIngredients().add(ing));
        }

        Recipe updatedRecipe = recipeRepository.save(existingRecipe);
        log.info("Recipe updated successfully with ID: {}", updatedRecipe.getId());

        return convertToResponse(updatedRecipe);
    }

    @Override
    public Optional<RecipeResponse> getRecipeById(Long id) {
        log.info("Fetching recipe by ID: {}", id);
        return recipeRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public List<RecipeResponse> getRecipesByRestaurant(Long restaurantId) {
        log.info("Fetching recipes for restaurant: {}", restaurantId);
        return recipeRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RecipePageResponse getRecipesByRestaurant(Long restaurantId, Pageable pageable) {
        log.info("Fetching recipes for restaurant: {} with pagination", restaurantId);
        Page<RecipeResponse> page = recipeRepository.findByRestaurantId(restaurantId, pageable)
                .map(this::convertToResponse);
        return new RecipePageResponse(
                page.getContent(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    @Override
    public RecipePageResponse getRecipesByRestaurant(Long restaurantId, Pageable pageable, String name) {
        log.info("Fetching recipes for restaurant: {} with search: {}", restaurantId, name);
        Page<RecipeResponse> page;
        if (name != null && !name.isBlank()) {
            page = recipeRepository.findByRestaurantIdAndNameContainingIgnoreCase(restaurantId, name.trim(), pageable)
                    .map(this::convertToResponse);
        } else {
            page = recipeRepository.findByRestaurantId(restaurantId, pageable)
                    .map(this::convertToResponse);
        }
        return new RecipePageResponse(
                page.getContent(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    @Override
    public void deleteRecipe(Long id) {
        log.info("Deleting recipe by ID: {}", id);
        recipeIngredientRepository.deleteByRecipeId(id);
        recipeRepository.deleteById(id);
    }

    @Override
    public boolean existsByRecipeId(String recipeId) {
        return recipeRepository.existsByRecipeId(recipeId);
    }

    @Override
    public RecipeProduction createProduction(Long recipeId, Long menuItemId, Long restaurantId, Double batchMultiplier, String note, Long createdBy) {
        log.info("Creating recipe production record for recipe: {}, menu item: {}", recipeId, menuItemId);

        RecipeProduction production = new RecipeProduction(
            recipeId,
            menuItemId,
            restaurantId,
            batchMultiplier != null ? BigDecimal.valueOf(batchMultiplier) : null,
            note,
            createdBy
        );

        RecipeProduction saved = recipeProductionRepository.save(production);
        log.info("Recipe production created with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public java.util.Map<String, Object> getRecipeProductionsByRestaurant(Long restaurantId, Pageable pageable, String name) {
        log.info("Fetching recipe productions for restaurant: {} with search: {}", restaurantId, name);

        Page<RecipeProduction> page = recipeProductionRepository.findByRestaurantId(restaurantId, pageable);

        List<RecipeProductionResponse> data = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("data", data);
        response.put("currentPage", page.getNumber() + 1);
        response.put("pageCount", page.getTotalPages());
        response.put("totalRowCount", page.getTotalElements());
        return response;
    }

    @Override
    public void produceRecipe(Long menuItemId, Double batchMultiplier, String note, Long createdBy) {
        log.info("Producing recipe for menu item: {} with batch multiplier: {}", menuItemId, batchMultiplier);

        Recipe recipe = recipeRepository.findAll((root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get("menuItemId"), menuItemId),
                criteriaBuilder.equal(root.get("isActive"), true)
            );
        }).stream().findFirst()
          .orElseThrow(() -> new ResourceNotFoundException("No active recipe found for menu item ID: " + menuItemId));

        log.info("Found recipe: {} (ID: {})", recipe.getName(), recipe.getId());

        RecipeProduction production = createProduction(recipe.getId(), menuItemId, recipe.getRestaurant() != null ? recipe.getRestaurant().getId() : null, batchMultiplier, note, createdBy);
        Long batchId = production.getId();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (ingredient.getInventoryItem() == null || ingredient.getQuantity() == null) {
                log.warn("Skipping ingredient with null inventory item or quantity: {}", ingredient.getIngredientName());
                continue;
            }

            InventoryItem inventoryItem = ingredient.getInventoryItem();
            BigDecimal requiredQty = ingredient.getQuantity().multiply(java.math.BigDecimal.valueOf(batchMultiplier));

            if (inventoryItem.getCurrentStock().compareTo(requiredQty) < 0) {
                throw new com.cafex.pos.exception.BadRequestException(
                    String.format("Insufficient stock for ingredient %s. Required: %s, Available: %s",
                        ingredient.getIngredientName(), requiredQty, inventoryItem.getCurrentStock())
                );
            }

            BigDecimal newStock = inventoryItem.getCurrentStock().subtract(requiredQty);
            inventoryItem.setCurrentStock(newStock);
            inventoryItem.setLastStockUpdate(LocalDateTime.now());
            inventoryItemRepository.save(inventoryItem);

            InventoryStockLog stockLog = new InventoryStockLog();
            stockLog.setInventoryItemId(inventoryItem.getId());
            stockLog.setInventoryItemName(inventoryItem.getName());
            stockLog.setRestaurantId(recipe.getRestaurant() != null ? recipe.getRestaurant().getId() : null);
            stockLog.setQuantityChange(requiredQty.negate());
            stockLog.setBalanceAfter(newStock);
            stockLog.setType("PRODUCTION");
            stockLog.setReferenceId(recipe.getId());
            stockLog.setReferenceType("RECIPE");
            stockLog.setBatchId(batchId);
            stockLog.setNote("Produced " + recipe.getName() + " (x" + batchMultiplier + ")" + (note != null ? ": " + note : ""));
            stockLog.setCreatedBy(createdBy);
            inventoryStockLogRepository.save(stockLog);

            log.info("Deducted {} of {} from inventory item {}. New stock: {}",
                requiredQty, ingredient.getIngredientName(), inventoryItem.getId(), newStock);
        }

        log.info("Production completed for recipe: {} ({} ingredients processed), batch ID: {}", recipe.getName(), recipe.getIngredients().size(), batchId);
    }

    private List<RecipeIngredient> buildIngredients(List<RecipeIngredientRequest> ingredientRequests, Recipe recipe) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (int i = 0; i < ingredientRequests.size(); i++) {
            RecipeIngredientRequest req = ingredientRequests.get(i);

            String ingredientId = req.getIngredientId();
            if (ingredientId == null || ingredientId.isBlank()) {
                ingredientId = "ing-" + System.currentTimeMillis() + "-" + i;
            }
            ingredientId = ingredientId.trim();

            String ingredientNameRaw = req.getIngredientName();
            if (ingredientNameRaw == null || ingredientNameRaw.isBlank()) {
                log.warn("Skipping ingredient at index {}: ingredientName is required", i);
                continue;
            }
            final String trimmedIngredientName = ingredientNameRaw.trim();

            BigDecimal quantity = req.getQuantity();
            if (quantity == null) {
                log.warn("Skipping ingredient '{}': quantity is required", trimmedIngredientName);
                continue;
            }

            String unit = req.getUnit();
            if (unit == null || unit.isBlank()) {
                unit = "pcs";
            }
            unit = unit.trim();

            RecipeIngredient ingredient = new RecipeIngredient();
            ingredient.setRecipe(recipe);
            ingredient.setIngredientId(ingredientId);
            ingredient.setIngredientName(trimmedIngredientName);
            ingredient.setQuantity(quantity);
            ingredient.setUnit(unit);
            ingredient.setIsOptional(req.getIsOptional() != null ? req.getIsOptional() : false);
            ingredient.setSubstituteAllowed(req.getSubstituteAllowed() != null ? req.getSubstituteAllowed() : false);
            ingredient.setSubstituteIngredient(req.getSubstituteIngredient());
            ingredient.setPreparationNotes(req.getPreparationNotes());
            ingredient.setCost(req.getCost());

            if (req.getInventoryItemId() != null) {
                final RecipeIngredient ingredientRef = ingredient;
                inventoryItemRepository.findById(req.getInventoryItemId()).ifPresentOrElse(
                        foundItem -> ingredientRef.setInventoryItem(foundItem),
                        () -> log.warn("Inventory item not found for ID {}, ingredient '{}' will be saved without inventory link",
                                req.getInventoryItemId(), trimmedIngredientName)
                );
            }

            ingredient.setCreatedAt(LocalDateTime.now());
            ingredient.setUpdatedAt(LocalDateTime.now());

            ingredients.add(ingredient);
        }

        return ingredients;
    }

    private RecipeResponse convertToResponse(Recipe recipe) {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setRecipeId(recipe.getRecipeId());
        response.setName(recipe.getName());
        response.setDescription(recipe.getDescription());
        response.setServingSize(recipe.getServingSize());
        response.setPreparationTimeMinutes(recipe.getPreparationTimeMinutes());
        response.setCookingTimeMinutes(recipe.getCookingTimeMinutes());
        response.setTotalTimeMinutes(recipe.getTotalTimeMinutes());
        response.setDifficultyLevel(recipe.getDifficultyLevel() != null ? recipe.getDifficultyLevel().name() : null);
        response.setIsActive(recipe.getIsActive());
        response.setMenuItemId(recipe.getMenuItemId());
        response.setRestaurantId(recipe.getRestaurant() != null ? recipe.getRestaurant().getId() : null);
        response.setCreatedAt(recipe.getCreatedAt());
        response.setUpdatedAt(recipe.getUpdatedAt());

        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            List<RecipeIngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                    .map(ing -> {
                        RecipeIngredientResponse res = new RecipeIngredientResponse();
                        res.setId(ing.getId());
                        res.setRecipeId(recipe.getId());
                        res.setIngredientId(ing.getIngredientId());
                        res.setInventoryItemId(ing.getInventoryItem() != null ? ing.getInventoryItem().getId() : null);
                        res.setIngredientName(ing.getIngredientName());
                        res.setQuantity(ing.getQuantity());
                        res.setUnit(ing.getUnit());
                        res.setCost(ing.getCost());
                        res.setIsOptional(ing.getIsOptional());
                        res.setSubstituteAllowed(ing.getSubstituteAllowed());
                        res.setSubstituteIngredient(ing.getSubstituteIngredient());
                        res.setPreparationNotes(ing.getPreparationNotes());
                        res.setCreatedAt(ing.getCreatedAt());
                        res.setUpdatedAt(ing.getUpdatedAt());
                        return res;
                    })
                    .collect(Collectors.toList());
            response.setIngredients(ingredientResponses);
        } else {
            response.setIngredients(new ArrayList<>());
        }

        return response;
    }

    private RecipeProductionResponse convertToResponse(RecipeProduction production) {
        RecipeProductionResponse response = new RecipeProductionResponse();
        response.setId(production.getId());
        response.setRecipeId(production.getRecipeId());
        response.setMenuItemId(production.getMenuItemId());
        response.setRestaurantId(production.getRestaurantId());
        response.setBatchMultiplier(production.getBatchMultiplier());
        response.setNote(production.getNote());
        response.setCreatedBy(production.getCreatedBy());
        response.setCreatedAt(production.getCreatedAt());
        return response;
    }
}
