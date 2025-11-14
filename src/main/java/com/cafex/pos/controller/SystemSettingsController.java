package com.cafex.pos.controller;

import com.cafex.pos.entity.SystemSetting;
import com.cafex.pos.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system-settings")
@CrossOrigin(origins = "*")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    /**
     * Get system settings
     */
    @GetMapping("/get-system-settings")
    public ResponseEntity<SystemSetting> getSystemSettings() {
        try {
            SystemSetting settings = systemSettingsService.getSystemSettings();
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Save system settings
     */
    @PutMapping("/save-system-settings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SystemSetting> saveSystemSettings(@RequestBody SystemSetting settings) {
        try {
            SystemSetting savedSettings = systemSettingsService.saveSystemSettings(settings);
            return ResponseEntity.ok(savedSettings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}

// java/api-cafe-x-pos-git/src/main/java/com/cafex/pos/controller/
// ├── AuthController.java                    # Authentication & authorization APIs
// ├── CommonController.java                    # Common utilities and shared APIs
// ├── PlatformController.java               # Platform-wide management APIs
// │   ├── System settings management
// │   ├── User management & roles
// │   ├── Platform notifications
// │   ├── Broadcast messages
// │   ├── System analytics
// │   ├── Audit logs
// │   └── Security configurations
// ├── RestaurantController.java             # Restaurant operations APIs
// │   ├── Restaurant profile management
// │   ├── Staff management
// │   ├── Table management
// │   ├── Shift scheduling & reports
// │   ├── Kitchen operations
// │   └── Restaurant analytics
// ├── OrderController.java                  # Order processing APIs
// │   ├── Order creation & management
// │   ├── Order items & customizations
// │   ├── Payment processing
// │   ├── Transaction management
// │   ├── Order status tracking
// │   └── Receipt generation
// ├── MenuController.java                   # Menu & inventory APIs
// │   ├── Menu item management
// │   ├── Menu categories
// │   ├── Inventory management
// │   ├── Recipe management
// │   ├── Stock adjustments
// │   └── Menu analytics
// ├── CustomerController.java               # Customer relationship APIs
// │   ├── Customer profiles
// │   ├── Loyalty programs
// │   ├── Customer preferences
// │   ├── Customer feedback
// │   └── Customer analytics
// ├── SubscriptionController.java           # Business subscription APIs
// │   ├── Subscription plans
// │   ├── Subscription management
// │   ├── Billing & invoices
// │   ├── Feature access control
// │   └── Usage tracking
// └── IntegrationController.java            # External integrations APIs
//     ├── Payment gateway configurations
//     ├── Third-party service integrations
//     ├── API rate limiting
//     ├── Webhook management
//     └── External API logs
