package com.cafex.pos.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column
    private String avatar;

    @Column(name = "restaurant_id")
    private String restaurantId;

    @Column(name = "member_since")
    private LocalDateTime memberSince;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_active", nullable = false)
    private ActiveStatus isActive = ActiveStatus.Y;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public enum UserRole {
        platform_owner,
        restaurant_owner,
        restaurant_manager,
        kitchen_manager,
        cashier,
        waiter,
        customer
    }

    public enum ActiveStatus {
        Y,
        N
    }

    public enum UserType {
        admin,
        custom,
        customer
    }
}