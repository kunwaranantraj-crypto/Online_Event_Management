# Online Event Management System

A comprehensive JavaFX-based event management system with role-based access control supporting Admin, Event Organizer, and Attendee roles. Built with MVC architecture and MySQL database backend.

## 🚀 Quick Start (Windows)

1. **Prerequisites**: Install Java 11+, Maven, and MySQL Server
2. **Setup Database**: Run `database/schema.sql` in MySQL
3. **Configure**: Update database credentials in `DatabaseConfig.java`
4. **Run**: Double-click `run.bat` or use `mvn javafx:run`

📖 **Detailed Setup**: See [SETUP_WINDOWS.md](SETUP_WINDOWS.md) for complete installation guide

## 🎯 Features

### 👨‍💼 Admin Dashboard
- **User Management**: Add, edit, delete users with role assignment
- **Event Approval**: Review and approve/reject event submissions
- **Analytics**: Real-time charts showing user distribution and event statistics
- **System Monitoring**: Activity logs and system health monitoring

### 🎪 Event Organizer Dashboard
- **Event Creation**: Create detailed events with date, time, venue, and descriptions
- **Ticket Management**: Multiple ticket types with pricing and quantity control
- **Communication**: Send messages and notifications to registered attendees
- **Sales Analytics**: Track ticket sales and registration trends

### 🎫 Attendee Dashboard
- **Event Discovery**: Browse and search approved events
- **Registration**: Easy event registration with ticket selection
- **Payment Simulation**: Simulated payment process for ticket purchases
- **Profile Management**: Update personal information and preferences
- **Notifications**: Receive messages from event organizers

## 🛠 Technical Architecture

### **Frontend**
- **JavaFX**: Modern desktop UI framework
- **FXML**: Declarative UI design
- **Charts**: Built-in JavaFX charts for analytics
- **Responsive Design**: Adaptive layouts for different screen sizes

### **Backend**
- **MVC Pattern**: Clean separation of concerns
- **DAO Pattern**: Database abstraction layer
- **MySQL Database**: Robust relational database
- **Prepared Statements**: SQL injection prevention

### **Security**
- **Role-Based Access**: Three distinct user roles with appropriate permissions
- **Input Validation**: Comprehensive form validation
- **SQL Injection Protection**: Parameterized queries throughout
- **Authentication**: Secure login system

## 📊 Database Schema

```sql
Users → Events → Tickets → Registrations
  ↓       ↓        ↓         ↓
Roles   Status   Pricing   Payments
```

**Key Tables:**
- `users` - User accounts with role-based access
- `events` - Event details with approval workflow
- `tickets` - Ticket types and pricing per event
- `registrations` - User event registrations with payment tracking
- `messages` - Communication between organizers and attendees

## 🎮 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Organizer | `organizer1` | `org123` |
| Attendee | `attendee1` | `att123` |

## 📁 Project Structure
```
event-management-system/
├── 📂 src/main/java/com/eventmanagement/
│   ├── 🚀 Main.java                    # Application entry point
│   ├── 📂 model/                       # Data models (User, Event, Ticket, etc.)
│   ├── 📂 view/                        # JavaFX UI components
│   ├── 📂 controller/                  # Business logic and UI controllers
│   ├── 📂 dao/                         # Database access objects
│   └── 📂 util/                        # Configuration and utilities
├── 📂 database/
│   ├── 📄 schema.sql                   # MySQL database schema
│   └── 📄 sqlite_schema.sql            # SQLite alternative
├── 📄 pom.xml                          # Maven dependencies
├── 📄 run.bat                          # Windows launcher
├── 📄 SETUP_WINDOWS.md                 # Detailed setup guide
└── 📄 README.md                        # This file
```

## 🔧 Development Features

- **Clean Code**: Well-documented, maintainable codebase
- **Error Handling**: Comprehensive exception handling
- **Logging**: System activity tracking
- **Extensible**: Easy to add new features and user roles
- **Cross-Platform**: Runs on Windows, macOS, and Linux

## 🚀 Running the Application

### Windows (Recommended)
```batch
# Quick start
run.bat

# Or manually
mvn clean compile
mvn javafx:run
```

### Command Line
```bash
# Compile
mvn clean compile

# Run
mvn javafx:run

# Package (creates executable JAR)
mvn clean package
```

## 📈 Performance & Scalability

- **Database Indexing**: Optimized queries with proper indexes
- **Connection Pooling**: Efficient database connection management
- **Lazy Loading**: On-demand data loading for better performance
- **Caching**: Strategic caching of frequently accessed data

## 🔮 Future Enhancements

- **Email Integration**: Automated email notifications
- **Payment Gateway**: Real payment processing integration
- **Mobile App**: Companion mobile application
- **Advanced Analytics**: Detailed reporting and insights
- **Multi-language**: Internationalization support
- **Cloud Deployment**: Web-based version with cloud hosting

## 📝 License

This project is created for educational purposes. Feel free to use and modify for learning and development.

---

**Built with ❤️ using JavaFX, MySQL, and Maven**