# HR Management System (HRMS)

A robust, full-stack Human Resource Management System built with **Spring Boot 3**, **Thymeleaf**, and **Tailwind CSS**. This system is designed to streamline employee management, attendance tracking with geofencing, and administrative reporting.

---

## 🌟 Key Features

### 1. Modern & Secure Login
- **Premium UI**: Elegant dark mesh gradient background with ambient particle animations.
- **Custom CAPTCHA**: In-house session-based CAPTCHA system to prevent automated bot access.
- **Glassmorphism Design**: High-readability interface with modern aesthetics.

### 2. Multi-Location Geofencing
- **Map Integration**: Visual geofencing management using Leaflet.js.
- **Reference Layers**: Prevents duplicate locations by showing existing office boundaries on the map during configuration.
- **Tooltips**: Instant identification of existing office locations on hover.

### 3. Employee & Attendance Lifecycle
- **Real-time Dashboard**: Live tracking of late arrivals and employees on leave.
- **Geofenced Check-in/Out**: Validates employee location against office boundaries.

### 4. Advanced Reporting & Export
- **Attendance History**: Centralized interface for Admins to view global attendance logs.
- **Flexible Filters**: Filter reports by **Date Range**, **NIK**, or **Employee Name**.
- **Professional Exports**: Generate high-fidelity **Excel (XLSX)** and **PDF** reports for auditing and payroll purposes.
- **Data Validation**: Visual indicators for geofence validity and status (Hadir, Terlambat, Outside Area).

---

## 🛠️ Technology Stack

- **Backend**: Java 17+, Spring Boot 3, Spring Data JPA
- **Database**: MySQL 8
- **Frontend**: Thymeleaf, Tailwind CSS, Javascript (ES6)
- **Mapping**: Leaflet.js & Leaflet Draw
- **Exporting**: Apache POI (Excel) & OpenPDF (PDF)
- **Icons**: FontAwesome 6

---

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0**

### 1. Database Configuration
1. Open your MySQL client and create the database:
   ```sql
   CREATE DATABASE hr_management_system_db;
   ```
2. Update the credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hr_management_system_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

### 2. Installation & Running
1. Clone the repository and navigate to the project root.
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the application at `http://localhost:8080`.

### 3. Default Credentials
- **Admin/Employee**: Use a valid NIK (e.g., `2026001`) and the password configured during setup (default seeder uses `bintangmada`).

---

## 🤝 Contributing
Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

---
© 2026 HR Management System - Bintang Mada
