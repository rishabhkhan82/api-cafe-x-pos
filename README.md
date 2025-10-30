# Cafe-X POS Backend

Enterprise Point of Sale System Backend API built with Spring Boot 3.4.11

## 🚀 Features

- **JWT Authentication** - Secure token-based authentication
- **Role-based Access Control** - Platform owner, restaurant owner, manager, staff, customer roles
- **MySQL Database** - Relational database with 76+ tables
- **RESTful APIs** - Complete CRUD operations for all entities
- **Spring Security** - Comprehensive security configuration
- **CORS Support** - Cross-origin resource sharing for frontend integration
- **Actuator Monitoring** - Health checks and application metrics

## 🛠️ Tech Stack

- **Java 17** - Programming language
- **Spring Boot 3.4.11** - Framework
- **Spring Data JPA** - ORM and database access
- **MySQL 8.4** - Database
- **JWT** - Authentication tokens
- **Maven** - Build tool
- **Lombok** - Code generation

## 📋 Prerequisites

- **Java JDK 17** or higher
- **MySQL Server 8.0+** running on port 3307
- **Maven 3.6+** (included with Spring Boot)

## ⚙️ Installation & Setup

### 1. Clone and Navigate
```bash
cd backend
```

### 2. Database Setup
```bash
# Create database
mysql -u root -p -P 3307 -e "CREATE DATABASE cafe_x_pos;"

# Default password: cafe_x_pos_2024
```

### 3. Build and Run
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## 🔐 Default Users

| Username | Password | Role |
|----------|----------|------|
| rishabh@cafex.com | password | RESTAURANT_OWNER |
| priya@cafex.com | password | RESTAURANT_MANAGER |
| amit@cafex.com | password | CASHIER |
| sneha@cafex.com | password | KITCHEN_MANAGER |
| rahul@cafex.com | password | WAITER |
| amit.patil@email.com | password | CUSTOMER |
| sarah.j@email.com | password | CUSTOMER |

## 📡 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Token validation
- `POST /api/auth/logout` - User logout

### Health Check
- `GET /actuator/health` - Application health status

## 🗄️ Database Schema

The application uses 76+ database tables organized in 7 major categories:

1. **Core Business Entities** - Users, restaurants, system configs
2. **Financial Management** - Transactions, payments, offers
3. **Human Capital Management** - Staff, shifts, performance
4. **Operations Management** - Orders, inventory, recipes
5. **Business Intelligence** - Analytics, reports
6. **Platform Infrastructure** - Security, integrations
7. **Customer Experience** - Loyalty, personalization

## 🔧 Configuration

### Database Connection
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/cafe_x_pos
spring.datasource.username=root
spring.datasource.password=cafe_x_pos_2024
```

### JWT Configuration
```properties
app.jwt.secret=cafe-x-pos-jwt-secret-key-2024-spring-boot
app.jwt.expiration=86400000
```

### CORS Configuration
```properties
app.cors.allowed-origins=http://localhost:4200,http://localhost:3000
```

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📊 Monitoring

- **Health Check**: `http://localhost:8080/actuator/health`
- **Application Info**: `http://localhost:8080/actuator/info`
- **Metrics**: `http://localhost:8080/actuator/metrics`

## 🚀 Deployment

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package
java -jar target/cafe-x-pos-backend-1.0.0.jar
```

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Add tests
4. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions, please contact the development team.