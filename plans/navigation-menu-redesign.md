# Navigation Menu APIs Redesign Plan

## Current System Analysis

### Existing Architecture
- **Entity**: `NavigationMenu` with hierarchical structure (parent_id, display_order, type: PARENT/ACTION)
- **Permissions**: `MenuAccessPermissions` linking menus to roles with CRUD permissions
- **APIs**: Basic CRUD operations with pagination and filtering
- **Issues**:
  - No tree-structured responses for hierarchical menus
  - No integrated role-based filtering in menu retrieval
  - Flat list responses don't represent menu hierarchy
  - No caching for frequently accessed menu data
  - Limited validation for menu hierarchy integrity

### Pain Points
1. **Hierarchical Data Handling**: Current APIs return flat lists, requiring client-side tree building
2. **Role-Based Filtering**: No direct API to get menus for a specific role with permissions
3. **Performance**: No caching for menu data that's accessed frequently
4. **Validation**: Limited business rule validation for menu hierarchies
5. **API Design**: CRUD-focused rather than menu-structure-focused

## Redesigned Architecture

### Core Principles
1. **Tree-First Design**: APIs that return hierarchical menu structures
2. **Role-Aware**: Built-in role-based filtering and permission checking
3. **Performance Optimized**: Caching layer for menu data
4. **Validation Heavy**: Comprehensive business rule validation
5. **RESTful Evolution**: Enhanced REST design with HATEOAS considerations

### New API Endpoints

#### 1. Menu Tree APIs
```
GET /api/navigation-menus/tree
GET /api/navigation-menus/tree/{role}
GET /api/navigation-menus/tree/user/{userId}
```

#### 2. Enhanced CRUD with Hierarchy
```
GET /api/navigation-menus/hierarchy
POST /api/navigation-menus/batch
PUT /api/navigation-menus/{id}/position
DELETE /api/navigation-menus/{id}/cascade
```

#### 3. Permission-Integrated APIs
```
GET /api/navigation-menus/accessible
POST /api/navigation-menus/{id}/permissions
GET /api/navigation-menus/{id}/permissions/{role}
```

#### 4. Management APIs
```
GET /api/navigation-menus/admin/tree
POST /api/navigation-menus/validate
GET /api/navigation-menus/orphaned
```

### New Data Structures

#### MenuTreeNode DTO
```java
public class MenuTreeNode {
    private Long id;
    private String menuId;
    private String name;
    private String path;
    private String icon;
    private Integer displayOrder;
    private MenuType type;
    private Boolean isActive;
    private MenuPermissions permissions; // For role-filtered responses
    private List<MenuTreeNode> children;
    private List<MenuAction> actions; // For PARENT nodes
}
```

#### MenuPermissions DTO
```java
public class MenuPermissions {
    private String role;
    private Boolean canView;
    private Boolean canEdit;
    private Boolean canDelete;
    private Boolean canCreate;
}
```

#### MenuValidationResult DTO
```java
public class MenuValidationResult {
    private Boolean isValid;
    private List<String> errors;
    private List<String> warnings;
    private MenuTreeNode validatedTree;
}
```

### Service Layer Enhancements

#### NavigationMenuTreeService
- `buildMenuTree()`: Construct hierarchical tree from flat data
- `getMenuTreeForRole(String role)`: Role-filtered tree building
- `getMenuTreeForUser(Long userId)`: User-specific menu tree
- `validateMenuHierarchy()`: Business rule validation
- `reorderMenuItems()`: Handle display order changes

#### NavigationMenuPermissionService
- `getPermissionsForMenuAndRole(Long menuId, String role)`
- `applyPermissionsToTree(MenuTreeNode tree, String role)`
- `validatePermissionConsistency()`

### Repository Enhancements

#### Custom Queries
- Find all root menus (parent_id IS NULL)
- Find children by parent_id with ordering
- Find menus by role with permissions
- Recursive queries for deep hierarchy validation

### Caching Strategy

#### Cache Keys
- `menu_tree`: Complete menu tree
- `menu_tree_{role}`: Role-specific trees
- `menu_permissions_{menuId}_{role}`: Individual permissions

#### Cache Configuration
- TTL: 30 minutes for menu trees
- TTL: 15 minutes for permissions
- Cache invalidation on menu/permission changes

### Validation Rules

#### Hierarchy Validation
1. No circular references in parent-child relationships
2. Display order uniqueness within same parent
3. PARENT type nodes cannot have direct actions (must have children or be leaf parents)
4. ACTION type nodes cannot have children
5. All active menus must have valid paths

#### Permission Validation
1. Permissions must exist for all role-menu combinations
2. At least 'canView' permission required for active menus
3. Parent permissions should propagate to children (configurable)

### Error Handling

#### Custom Exceptions
- `MenuHierarchyException`: For hierarchy violations
- `MenuPermissionException`: For permission issues
- `MenuValidationException`: For validation failures

#### HTTP Status Codes
- 200: Success
- 400: Validation errors
- 403: Insufficient permissions
- 404: Menu not found
- 409: Hierarchy conflicts
- 422: Business rule violations

### Implementation Phases

#### Phase 1: Core Tree APIs
1. Implement MenuTreeNode and related DTOs
2. Create NavigationMenuTreeService
3. Add tree-building endpoints
4. Basic caching implementation

#### Phase 2: Permission Integration
1. Implement permission-aware tree building
2. Add role-based filtering endpoints
3. Permission validation and caching

#### Phase 3: Advanced Features
1. Batch operations
2. Hierarchy validation
3. Admin management APIs
4. Comprehensive error handling

#### Phase 4: Optimization
1. Advanced caching strategies
2. Query optimization
3. Performance monitoring
4. API documentation

### Migration Strategy

1. **Parallel Implementation**: New APIs alongside existing ones
2. **Gradual Migration**: Update clients to use new APIs
3. **Backward Compatibility**: Keep existing APIs functional
4. **Data Migration**: Ensure existing data conforms to new validation rules

### Testing Strategy

#### Unit Tests
- Tree building algorithms
- Permission application logic
- Validation rules
- Cache operations

#### Integration Tests
- End-to-end API flows
- Database operations
- Caching behavior
- Permission enforcement

#### Performance Tests
- Large menu tree building
- Concurrent access patterns
- Cache hit/miss ratios

### Monitoring and Observability

#### Metrics
- Menu tree build time
- Cache hit/miss ratios
- Permission check latency
- API response times

#### Logging
- Menu hierarchy changes
- Permission modifications
- Validation failures
- Performance bottlenecks

This redesign transforms the navigation menu system from a basic CRUD API into a sophisticated, hierarchy-aware, permission-integrated menu management system optimized for modern web applications.