# LifeLink - Organ Donation Management System

LifeLink is a comprehensive organ donation management system designed to streamline and secure the organ donation process. The application provides a platform for potential donors to register, receivers to request organs, and administrators to manage the donation process.

## Features

- User management (Donors, Receivers, Administrators)
- Organ donation registration
- Organ request management
- Advanced organ matching algorithm
- Secure authentication with JWT
- Role-based access control

## Technology Stack

- Java 11
- Spring Boot 2.7.0
- Spring Security with JWT
- Spring Data JPA
- MySQL Database
- Lombok
- Maven

## Prerequisites

To run the application locally, you need:

- JDK 11 or higher
- MySQL Server 8.0 or higher
- Maven (or use the provided Maven wrapper)

## Running the Application Locally

1. **Database Setup**:
   - Make sure MySQL is running
   - By default, the application expects a database named `lifelink` with username `root` and password `root`
   - You can modify these settings in `src/main/resources/application.properties`

2. **Building and Running**:
   - Using the Maven wrapper (recommended):
     ```
     ./mvnw spring-boot:run
     ```
   - Or use the provided script:
     ```
     ./run.sh
     ```

3. **Access the Application**:
   - The application will be available at `http://localhost:8080/lifelink`
   - Swagger API documentation: `http://localhost:8080/lifelink/swagger-ui.html`

## API Endpoints

- **Authentication**:
  - POST `/api/auth/signin` - Login
  - POST `/api/auth/signup` - Register

- **Donor Operations**:
  - GET `/api/donors` - Get all donors
  - POST `/api/donors` - Register as a donor
  - GET `/api/donors/{id}` - Get donor details
  - POST `/api/donors/{id}/donations` - Register organ donation

- **Receiver Operations**:
  - GET `/api/receivers` - Get all receivers
  - POST `/api/receivers` - Register as a receiver
  - GET `/api/receivers/{id}` - Get receiver details
  - POST `/api/receivers/{id}/requests` - Request organ

- **Admin Operations**:
  - GET `/api/admin/donations` - Get all donations
  - GET `/api/admin/requests` - Get all requests
  - PUT `/api/admin/donations/{id}` - Update donation status
  - PUT `/api/admin/requests/{id}` - Update request status
  - POST `/api/admin/match` - Run matching algorithm

## Default Users

On startup, the application creates the following default roles:
- ROLE_USER
- ROLE_DONOR
- ROLE_RECEIVER
- ROLE_ADMIN

## License

This project is licensed under the MIT License - see the LICENSE file for details.