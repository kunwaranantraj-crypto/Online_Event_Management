# Event Management System - Windows Setup Guide

## Prerequisites

### 1. Install Java Development Kit (JDK) 11 or higher
- Download from: https://www.oracle.com/java/technologies/downloads/
- Or use OpenJDK: https://adoptium.net/
- Add Java to your PATH environment variable

### 2. Install Apache Maven
- Download from: https://maven.apache.org/download.cgi
- Extract to a folder (e.g., C:\apache-maven-3.9.5)
- Add Maven bin directory to your PATH environment variable
- Verify installation: `mvn --version`

### 3. Install MySQL Server
- Download MySQL Community Server: https://dev.mysql.com/downloads/mysql/
- Install MySQL Server and MySQL Workbench
- During installation, set root password (remember this!)
- Start MySQL service

### 4. Install JavaFX SDK (if not included with JDK)
- Download from: https://openjfx.io/
- Extract to a folder
- Note the path for later configuration

## Database Setup

### 1. Create Database
Open MySQL Workbench or MySQL Command Line Client and run:

```sql
CREATE DATABASE event_management;
USE event_management;
```

### 2. Run Schema Script
Execute the SQL script located at `database/schema.sql` in MySQL Workbench:
- Open MySQL Workbench
- Connect to your MySQL server
- Open the `database/schema.sql` file
- Execute the script (Ctrl+Shift+Enter)

### 3. Configure Database Connection
Edit `src/main/java/com/eventmanagement/util/DatabaseConfig.java`:
- Update `DB_USERNAME` (default: "root")
- Update `DB_PASSWORD` (your MySQL root password)
- Update `DB_URL` if using different host/port

## Running the Application

### Method 1: Using Batch File (Recommended)
1. Double-click `run.bat`
2. The application will compile and start automatically

### Method 2: Using Command Line
1. Open Command Prompt in project directory
2. Run: `mvn clean compile`
3. Run: `mvn javafx:run`

### Method 3: Using IDE
1. Import project into IntelliJ IDEA or Eclipse
2. Configure JavaFX module path if needed
3. Run `com.eventmanagement.Main` class

## Default Login Credentials

After running the schema script, you can login with:

**Admin Account:**
- Username: `admin`
- Password: `admin123`

**Organizer Account:**
- Username: `organizer1`
- Password: `org123`

**Attendee Account:**
- Username: `attendee1`
- Password: `att123`

## Troubleshooting

### Common Issues:

1. **"JavaFX runtime components are missing"**
   - Solution: Add JavaFX modules to VM options:
   ```
   --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml
   ```

2. **Database Connection Failed**
   - Check MySQL service is running
   - Verify credentials in DatabaseConfig.java
   - Ensure database `event_management` exists

3. **Maven not found**
   - Verify Maven is installed and in PATH
   - Restart Command Prompt after PATH changes

4. **Compilation errors**
   - Ensure JDK 11+ is installed
   - Check JAVA_HOME environment variable

### Performance Tips:
- Increase JVM heap size for large datasets: `-Xmx1024m`
- Use MySQL indexes (already included in schema)
- Regular database maintenance and backups

## Project Structure
```
event-management-system/
├── src/main/java/com/eventmanagement/
│   ├── Main.java                 # Application entry point
│   ├── model/                    # Data models
│   ├── view/                     # JavaFX UI components
│   ├── controller/               # Business logic controllers
│   ├── dao/                      # Database access objects
│   └── util/                     # Utility classes
├── database/
│   ├── schema.sql               # MySQL database schema
│   └── sqlite_schema.sql        # SQLite alternative
├── pom.xml                      # Maven configuration
├── run.bat                      # Windows run script
└── README.md                    # Project documentation
```

## Features Overview

### Admin Dashboard
- User management (CRUD operations)
- Event approval system
- System statistics and charts
- Activity monitoring

### Organizer Dashboard
- Event creation and management
- Ticket type management
- Attendee communication
- Sales analytics

### Attendee Dashboard
- Event browsing and search
- Registration and ticket purchase
- Profile management
- Message notifications

## Security Features
- Role-based access control
- SQL injection prevention (prepared statements)
- Input validation
- Password protection (basic implementation)

For production use, consider implementing:
- Password hashing (BCrypt)
- Session management
- HTTPS/SSL encryption
- Advanced authentication (JWT tokens)