# SpotFlow - Smart Parking Management SaaS

SpotFlow is a professional, custom-built smart parking management SaaS platform. Transformed from a basic template application, SpotFlow features a premium visual theme, real-time parking slot grid representations, interactive reservation flows, automated checkouts, user billing estimations, and a unified administrator workspace.

---

## 🎨 Redesigned UI/UX Highlights

1. **Brand Identity**: Rebranded as **SpotFlow** with modern typography and styled assets.
2. **Visual Palette**: Premium slate/teal theme using high-contrast slate navy background (`#0F172A`), teal elements (`#14B8A6`), soft layout borders, and dynamic active nav indicators.
3. **Typography**: Hand-picked Google Fonts (`Inter` for base reading, `Poppins` for dashboard cards and operation panels).
4. **Split-Screen Authentication**: The Login and Sign-Up forms feature a 50/50 desktop split panel, showcasing a high-quality smart-parking vector illustration on the left, and a sleek form on the right.
5. **Interactive Dashboard**: Modular dark sidebar navigation menu, top status panels, and a real-time responsive slots occupancy grid.
6. **Billing & Receipts**: Fully redesigned invoice sheets with cost estimation calculators, cash/card/UPI payment dropdowns, and confirmation modal receipts.
7. **Profile Timeline**: Profile configurations showing client info, with recent parking history styled as timeline items.
8. **Responsive Layouts**: Designed to be responsive, adapting to mobile, tablet, and desktop viewports.

---

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.3.2 (Java 21)
- **Database Engine**: Neon Serverless PostgreSQL (or local MySQL / H2 databases)
- **Object-Relational Mapping (ORM)**: Spring Data JPA (Hibernate)
- **Frontend Template Engine**: Thymeleaf (HTML5 / Vanilla CSS3 / JavaScript UI)
- **Security Protocols**: Spring Security with encrypted password hashing (BCrypt)
- **Build Automator**: Maven

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java**: JDK 21 or higher installed.
- **Maven**: Maven 3.6+ (or use the included wrapper `./mvnw`).
- **Database**: A Neon PostgreSQL cluster (or local MySQL).

### 2. Configure Database Connections
Update the connection variables in `src/main/resources/application.properties` to connect to your database.

#### For Neon PostgreSQL (Default Configured):
```properties
spring.datasource.url=jdbc:postgresql://[YOUR_NEON_HOST]/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=[YOUR_PASSWORD]
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### For Local MySQL:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/parking_db?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 3. Build and Run the App

#### Local Execution:
```bash
# Navigate to the project folder and run with Maven Wrapper
./mvnw spring-boot:run

# Or compile and package into a runnable JAR
./mvnw clean package
java -jar target/parking-management-1.0.0.jar
```

#### Access Details:
- **Default Port**: `8081` (configurable in properties).
- **Public Workspace URL**: `http://localhost:8081`
- **Default Admin Account**:
  - **Username**: `admin`
  - **Password**: `admin123`
- **Default User Account**:
  - **Username**: `john_doe`
  - **Password**: `password123`

---

## 🔌 Core Rest API Endpoints

### 1. Booking API
- `POST /api/bookings/book/{slotId}?vehicleNumber={number}` - Reserve a specific parking slot.
- `POST /api/bookings/checkout/{bookingId}?paymentMethod={method}` - Process payment checkout (CASH/CARD/UPI).
- `GET /api/bookings/my/active` - List active bookings for the logged-in user.
- `GET /api/bookings/recent` - List recent booking logs for client profiles.

### 2. Slots API
- `GET /api/slots/available` - Fetch all vacant slots.
- `GET /api/slots/available/{vehicleType}` - Filter vacant slots by type (CAR/BIKE/TRUCK).
- `GET /api/slots/stats` - Fetch real-time available/total count statistics.

### 3. Administrator Console API
- `GET /api/admin/users` - Retrieve system users list.
- `POST /api/admin/users/{userId}/block?block={boolean}` - Restrict or grant user access permissions.
- `DELETE /api/admin/users/{userId}` - Erase user record.
- `GET /api/admin/pricing` - Retrieve price rates list.
- `POST /api/admin/pricing` - Commit updated CAR, BIKE, or TRUCK hourly rates.

---

## 📊 Sample Data Seeding
On startup, the system automatically runs schema seeding if database tables are empty:
- **Users**: Creates `admin` (Super User), `john_doe` (Client), and `jane_smith` (Client).
- **Slots**: Creates 40 custom parking slots:
  - Slots **C001** to **C020**: Cars section (Ground Floor, ₹50.0/hr).
  - Slots **B001** to **B015**: Bikes section (Ground Floor, ₹25.0/hr).
  - Slots **T001** to **T005**: Heavy trucks section (Basement, ₹100.0/hr).

---

## 🧪 Testing and Quality Control
Ensure code reliability by running the Junit test suite:
```bash
./mvnw test
```

---

## 🤝 Contributing
1. Fork this repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

**Happy Parking with SpotFlow! 🅿️**
