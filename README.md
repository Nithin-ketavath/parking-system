# Parking Management System

A comprehensive Spring Boot application for managing parking slots, bookings, and payments with a modern web interface.

## 🚀 Features

- **Slot Management**: Create, update, and manage parking slots
- **Booking System**: Book, check-in, and check-out parking slots
- **Payment Processing**: Handle payments with multiple payment methods
- **User Management**: User registration and authentication
- **Real-time Updates**: Live slot availability and booking status
- **Responsive UI**: Modern, mobile-friendly interface
- **REST API**: Complete RESTful API for all operations

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.3.2, Spring Security, Spring Data JPA
- **Database**: MySQL 8.0
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Build Tool**: Maven
- **Java Version**: 21

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Quick Start

### 1. Database Setup

Create a MySQL database:
```sql
CREATE DATABASE parkingdb;
```

### 2. Configuration

Update `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Run the Application

```bash
# Navigate to project directory
cd parking-system

# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/parking-management-1.0.0.jar
```

### 4. Access the Application

- **Web Interface**: http://localhost:8080
- **API Documentation**: http://localhost:8080/actuator
- **Default Login**: 
  - Username: `admin`
  - Password: `admin123`

## 📁 Project Structure

```
parking-system/
├── src/main/java/com/apc/parking/
│   ├── ParkingManagementApplication.java
│   ├── controller/
│   │   ├── BookingController.java
│   │   ├── SlotController.java
│   │   └── WebController.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── ParkingSlot.java
│   │   ├── Booking.java
│   │   ├── Payment.java
│   │   ├── Role.java
│   │   ├── SlotStatus.java
│   │   ├── BookingStatus.java
│   │   └── PaymentStatus.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ParkingSlotRepository.java
│   │   ├── BookingRepository.java
│   │   └── PaymentRepository.java
│   ├── service/
│   │   ├── BookingService.java
│   │   ├── BookingServiceImpl.java
│   │   └── DataInitializationService.java
│   └── security/
│       └── WebSecurityConfig.java
├── src/main/resources/
│   ├── application.properties
│   └── templates/
│       ├── index.html
│       ├── booking.html
│       ├── checkout.html
│       └── login.html
└── pom.xml
```

## 🔌 API Endpoints

### Booking Management
- `POST /api/bookings/book/{userId}/{slotId}` - Book a parking slot
- `POST /api/bookings/checkin/{bookingId}` - Check in to a slot
- `POST /api/bookings/checkout/{bookingId}` - Check out from a slot
- `POST /api/bookings/cancel/{bookingId}` - Cancel a booking
- `GET /api/bookings/user/{userId}` - Get user's bookings
- `GET /api/bookings/active` - Get active bookings
- `GET /api/bookings/{bookingId}` - Get booking by ID

### Slot Management
- `GET /api/slots` - Get all parking slots
- `GET /api/slots/{slotId}` - Get slot by ID
- `GET /api/slots/available` - Get available slots
- `GET /api/slots/available/{vehicleType}` - Get available slots by vehicle type
- `GET /api/slots/status/{status}` - Get slots by status
- `POST /api/slots` - Create new slot
- `PUT /api/slots/{slotId}` - Update slot
- `DELETE /api/slots/{slotId}` - Delete slot

## 💰 Pricing

- **Car**: ₹50/hour
- **Bike**: ₹25/hour  
- **Truck**: ₹100/hour
- **Minimum**: 1 hour charge

## 🎯 Usage Examples

### Book a Parking Slot
```bash
curl -X POST "http://localhost:8080/api/bookings/book/1/1?vehicleNumber=MH12AB1234"
```

### Check In
```bash
curl -X POST "http://localhost:8080/api/bookings/checkin/1"
```

### Check Out
```bash
curl -X POST "http://localhost:8080/api/bookings/checkout/1"
```

### Get Available Slots
```bash
curl -X GET "http://localhost:8080/api/slots/available"
```

## 🔐 Security

- Spring Security integration
- Password encryption with BCrypt
- Role-based access control
- CSRF protection
- Session management

## 🧪 Testing

Run tests with Maven:
```bash
mvn test
```

## 📊 Sample Data

The application automatically creates sample data on startup:
- 3 sample users (1 admin, 2 regular users)
- 40 parking slots (20 cars, 15 bikes, 5 trucks)
- Some pre-occupied slots for demonstration

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/parking-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Configuration
- Update database connection for production
- Configure proper security settings
- Set up SSL/TLS certificates
- Configure logging levels

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the API endpoints

## 🔄 Version History

- **v1.0.0** - Initial release with core functionality
  - Slot management
  - Booking system
  - Payment processing
  - Web interface
  - REST API

---

**Happy Parking! 🅿️**














