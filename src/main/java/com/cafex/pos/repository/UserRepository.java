package com.cafex.pos.repository;

import com.cafex.pos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(User.UserRole role);

    List<User> findByRestaurantId(String restaurantId);

    List<User> findByRoleAndRestaurantId(User.UserRole role, String restaurantId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role IN :roles AND u.restaurantId = :restaurantId")
    List<User> findStaffByRolesAndRestaurant(
        @Param("roles") List<User.UserRole> roles,
        @Param("restaurantId") String restaurantId
    );

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.UserRole role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.restaurantId = :restaurantId AND u.isActive = true")
    Long countActiveUsersByRestaurant(@Param("restaurantId") String restaurantId);
}