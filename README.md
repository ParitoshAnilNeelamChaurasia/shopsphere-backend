# ShopSphere Backend

Spring Boot + MySQL backend for the ShopSphere React frontend.

## Stack
- Java 21
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security + JWT
- MySQL

## Local setup

1. Create the database in MySQL:

```sql
CREATE DATABASE shopsphere_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Set the MySQL password. The application defaults to user `root` and an empty password. If your root user has a password, use an environment variable:

PowerShell:

```powershell
$env:DB_PASSWORD="your_mysql_password"
```

3. Start from IntelliJ IDEA using Maven, or from a terminal with Maven installed:

```powershell
mvn spring-boot:run
```

4. Health check:

`GET http://localhost:8080/api/health`

5. Frontend should use:

`VITE_API_BASE_URL=http://localhost:8080/api`
`VITE_USE_MOCK_API=false`

## Demo users

Customer:
- email: demo@shopsphere.com
- password: demo123

Admin:
- email: admin@shopsphere.com
- password: admin123

The users and products are inserted automatically on first application startup.
