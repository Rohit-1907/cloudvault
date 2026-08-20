# 📚 CloudVault - Complete Project Explanation

## 🎯 Project Overview

**CloudVault** is a secure file storage and sharing system where users can:
1. Upload files with automatic encryption
2. Set expiry time (5 min to 7 days)
3. Get a shareable link
4. Files automatically delete after expiry

**Think of it like:** A secure version of WeTransfer or Dropbox links that expire automatically.

---

## 🛠️ Technology Stack Explained

### 1. **Java 21** - Programming Language
- **What it is:** The main programming language for backend logic
- **Why we use it:** 
  - Object-oriented, secure, and widely used in enterprise
  - Strong type system prevents bugs
  - Great for building web applications
- **In this project:** All business logic, file handling, encryption

### 2. **Apache Maven** - Build & Dependency Management Tool
- **What it is:** A tool that manages your project's libraries and builds your application
- **Why we use it:**
  - Automatically downloads required libraries (like Spring Boot, PostgreSQL driver, etc.)
  - Compiles Java code into executable format
  - Manages project structure
  - Runs the application

**Analogy:** Think of Maven like a chef's assistant who:
- Gets all ingredients (libraries) from the store
- Prepares everything (compiles code)
- Serves the dish (runs the app)

**Key Maven commands:**
```bash
mvn clean           # Clean old compiled files
mvn install         # Download dependencies and compile
mvn spring-boot:run # Run the application
```

**pom.xml file:** This is Maven's recipe book. It lists:
- Project details (name, version)
- Dependencies (libraries needed)
- Build instructions

### 3. **Spring Boot 3.1.5** - Java Framework
- **What it is:** A framework that makes building web applications easier
- **Why we use it:**
  - Automatic configuration (less setup code)
  - Built-in web server (Tomcat)
  - Easy REST API creation
  - Dependency injection
  - Production-ready features

**Key Spring Boot Components in Our Project:**

#### a) **Spring Web** - Web Application
- Creates REST APIs
- Handles HTTP requests/responses
- Controllers for different endpoints

#### b) **Spring Data JPA** - Database Access
- Makes database operations easy
- No need to write SQL queries manually
- Object-Relational Mapping (ORM)

#### c) **Spring Boot DevTools** - Development Helper
- Auto-restarts application when code changes
- Faster development cycle

### 4. **PostgreSQL** (via Supabase) - Database
- **What it is:** Relational database to store file metadata
- **Why we use it:**
  - Reliable and scalable
  - ACID compliant (data integrity)
  - Free tier available via Supabase
- **What we store:**
  - File information (name, size, upload date)
  - Share tokens
  - Expiry dates
  - Access modes

### 5. **Hibernate** - ORM (Object-Relational Mapping)
- **What it is:** Converts Java objects to database tables automatically
- **Why we use it:**
  - No manual SQL writing for basic operations
  - Type-safe database operations
  - Automatic table creation
- **Example:**
```java
@Entity
public class CloudFile {
    @Id
    private Long id;
    private String fileName;
    // Hibernate creates table automatically!
}
```

### 6. **Supabase** - Backend as a Service
- **What it is:** Provides PostgreSQL database in the cloud
- **Why we use it:**
  - Free tier available
  - Easy setup (no server management)
  - PostgreSQL database
  - API access
- **Used for:** Storing file metadata

### 7. **HikariCP** - Database Connection Pool
- **What it is:** Manages database connections efficiently
- **Why we use it:**
  - Reuses database connections (faster)
  - Handles multiple users simultaneously
  - Automatic connection management

### 8. **Lombok** - Code Generator
- **What it is:** Reduces boilerplate code
- **Why we use it:**
  - Auto-generates getters/setters
  - Auto-generates constructors
  - Cleaner code
- **Example:**
```java
@Data  // Lombok generates getters, setters, toString, etc.
public class FileUploadResponse {
    private String shareLink;
    private String fileName;
}
```

---

## 🏗️ Project Architecture

### **MVC Pattern (Model-View-Controller)**

```
┌─────────────────────────────────────────────────┐
│                   USER/BROWSER                  │
│           (HTML/CSS/JavaScript Frontend)        │
└────────────────────┬────────────────────────────┘
                     │ HTTP Request
                     ▼
┌─────────────────────────────────────────────────┐
│              CONTROLLER LAYER                   │
│   (FileController, PublicShareController)       │
│   - Handles HTTP requests                       │
│   - Routes to appropriate service               │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│               SERVICE LAYER                     │
│  (FileService, EncryptionService, etc.)         │
│  - Business logic                               │
│  - File encryption/decryption                   │
│  - Share link generation                        │
└────────────────────┬────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         ▼                       ▼
┌──────────────────┐    ┌──────────────────┐
│  REPOSITORY      │    │  FILE SYSTEM     │
│  (Database)      │    │  (Local Storage) │
│  - File metadata │    │  - Encrypted     │
│  - Share tokens  │    │    files         │
└──────────────────┘    └──────────────────┘
```

---

## 📁 Project Structure Explained

```
cloudvault/
│
├── pom.xml                          # Maven configuration (dependencies)
│
├── src/main/java/com/cloudvault/
│   │
│   ├── CloudVaultApplication.java   # Main entry point (@SpringBootApplication)
│   │
│   ├── config/                      # Configuration classes
│   │   └── SupabaseConfig.java      # Supabase connection config
│   │
│   ├── controller/                  # REST API endpoints (handles HTTP)
│   │   ├── FileController.java      # File upload/download APIs
│   │   ├── PublicShareController.java # Public share page routing
│   │   └── AdminController.java     # Admin endpoints
│   │
│   ├── service/                     # Business logic layer
│   │   ├── FileService.java         # File operations logic
│   │   ├── EncryptionService.java   # AES-256 encryption/decryption
│   │   ├── CloudStorageService.java # File system operations
│   │   └── FileExpirySchedulerService.java # Auto-delete expired files
│   │
│   ├── repository/                  # Database access layer
│   │   └── CloudFileRepository.java # JPA repository for database
│   │
│   ├── model/                       # Data models (entities)
│   │   ├── CloudFile.java           # File entity (maps to DB table)
│   │   ├── FileStatus.java          # Enum for file status
│   │   └── AccessMode.java          # Enum for access modes
│   │
│   ├── dto/                         # Data Transfer Objects (API responses)
│   │   ├── FileUploadRequest.java
│   │   ├── FileUploadResponse.java
│   │   ├── FileAccessResponse.java
│   │   └── EncryptionVerificationResponse.java
│   │
│   ├── exception/                   # Error handling
│   │   └── GlobalExceptionHandler.java
│   │
│   └── util/                        # Utility classes
│       ├── NetworkUtils.java        # IP detection
│       └── SupabaseConnectionValidator.java # DB health check
│
└── src/main/resources/
    ├── application.properties       # App configuration (DB, encryption)
    └── static/                      # Frontend files
        ├── index.html               # Upload page
        ├── share.html               # Share page
        ├── script.js                # Frontend logic
        └── style.css                # Styling
```

---

## 🔄 How the Application Works (Step by Step)

### **Scenario 1: User Uploads a File**

```
1. USER ACTION:
   - Opens http://localhost:8080
   - Selects a file
   - Chooses expiry time (e.g., 1 hour)
   - Clicks "Upload File"

2. FRONTEND (script.js):
   - Validates file
   - Sends HTTP POST to /api/files/upload

3. CONTROLLER (FileController.java):
   @PostMapping("/upload")
   - Receives file
   - Calls FileService.uploadFile()

4. SERVICE (FileService.java):
   a) Generate unique share token (UUID)
   b) Call EncryptionService.encrypt(file)
   c) Call CloudStorageService.saveFile(encryptedFile)
   d) Save metadata to database via repository
   e) Generate share link
   f) Return FileUploadResponse

5. ENCRYPTION (EncryptionService.java):
   - Uses AES-256-CBC algorithm
   - Encrypts file bytes
   - Returns encrypted bytes

6. STORAGE (CloudStorageService.java):
   - Saves encrypted file to disk
   - Location: cloudvault-storage/uploads/

7. DATABASE (CloudFileRepository):
   - Saves file metadata:
     * file_name
     * share_token
     * expiry_date
     * file_size
     * access_mode

8. RESPONSE to USER:
   - Share link: http://10.220.117.150:8080/file/{token}
   - File info
   - Success message
```

### **Scenario 2: Recipient Opens Share Link**

```
1. USER ACTION:
   - Clicks: http://10.220.117.150:8080/file/abc123...

2. CONTROLLER (PublicShareController.java):
   @GetMapping("/file/{token}")
   - Receives token
   - Redirects to /share.html?token=abc123...

3. FRONTEND (share.html + script.js):
   - Extracts token from URL
   - Calls /api/files/access/{token}

4. SERVICE (FileService.java):
   - Checks if token exists in database
   - Checks if file expired
   - Returns file information

5. DISPLAY to USER:
   - File name
   - File size
   - Upload date
   - Expiry time
   - Download button (if allowed)

6. USER CLICKS DOWNLOAD:
   - Calls /api/files/download/{token}
   - FileService decrypts file
   - Returns decrypted file bytes
   - Browser downloads file
```

### **Scenario 3: File Auto-Deletion (Background)**

```
1. SCHEDULER (FileExpirySchedulerService.java):
   @Scheduled(fixedRate = 300000)  // Every 5 minutes
   - Runs automatically in background

2. LOGIC:
   - Find all files where expiry_date < current_time
   - For each expired file:
     a) Delete from file system
     b) Delete from database

3. RESULT:
   - Expired files automatically cleaned up
   - No manual intervention needed
```

---

## 🔐 Key Technologies & Concepts

### **1. Dependency Injection (Spring)**
```java
// Instead of creating objects manually:
// FileService service = new FileService(new Repository(), new Storage());

// Spring automatically injects dependencies:
@Service
public class FileService {
    private final CloudFileRepository repository;
    
    // Spring auto-creates and injects repository
    public FileService(CloudFileRepository repository) {
        this.repository = repository;
    }
}
```

### **2. JPA/Hibernate Annotations**
```java
@Entity  // Maps to database table
@Table(name = "cloud_files")
public class CloudFile {
    
    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)  // NOT NULL constraint
    private String fileName;
    
    @Column(unique = true)  // UNIQUE constraint
    private String shareToken;
}
```

### **3. REST API Endpoints**
```java
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    // POST http://localhost:8080/api/files/upload
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(...) { }
    
    // GET http://localhost:8080/api/files/access/{token}
    @GetMapping("/access/{token}")
    public ResponseEntity<FileAccessResponse> getFileAccess(...) { }
}
```

### **4. Encryption (AES-256)**
```java
// Key: 256-bit secret key
// Algorithm: AES (Advanced Encryption Standard)
// Mode: CBC (Cipher Block Chaining)
// Result: Encrypted bytes that look like random data

Original file: "Hello World.pdf"
Encrypted: [Binary gibberish - unreadable without key]
```

---

## 📦 Maven Dependencies Explained

From `pom.xml`:

```xml
<!-- Spring Boot Starter Web: Web application + REST API + Tomcat server -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Boot Starter Data JPA: Database access + Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- PostgreSQL Driver: Connect to PostgreSQL database -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Lombok: Reduce boilerplate code -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- DevTools: Auto-restart during development -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

---

## 🚀 How to Run the Project

### **Prerequisites:**
1. Java 17 or higher
2. Maven 3.6+
3. Supabase account (for database)

### **Setup Steps:**

```bash
# 1. Clone repository
git clone https://github.com/Rohit-1907/cloudvault.git
cd cloudvault

# 2. Configure database
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit application.properties with your Supabase credentials

# 3. Run application
mvn spring-boot:run

# 4. Access application
# Open browser: http://localhost:8080
```

---

## 🎓 Learning Resources

### **To understand Spring Boot:**
- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Baeldung Spring Tutorial](https://www.baeldung.com/spring-boot)

### **To understand Maven:**
- [Maven in 5 Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

### **To understand JPA/Hibernate:**
- [JPA Tutorial](https://www.baeldung.com/jpa-hibernate-difference)

---

## 💡 Key Takeaways for Your Team

1. **Java + Spring Boot**: Modern way to build web applications in Java
2. **Maven**: Manages all libraries and builds the project
3. **MVC Architecture**: Clean separation of concerns
4. **REST API**: Frontend talks to backend via HTTP
5. **JPA/Hibernate**: Easy database operations without SQL
6. **Security**: AES-256 encryption for all files
7. **Automation**: Scheduled tasks for cleanup

---

## ❓ Common Questions

**Q: Why Spring Boot instead of plain Java?**
A: Spring Boot provides auto-configuration, built-in web server, and many features out-of-the-box, saving development time.

**Q: Why Maven?**
A: Maven manages dependencies automatically. Without it, you'd manually download 50+ jar files!

**Q: Why PostgreSQL?**
A: Reliable, scalable, and free via Supabase. Great for production applications.

**Q: Why encrypt files?**
A: Security best practice. Even if someone accesses the storage directory, they can't read the files.

---

**Made by:** Rohit-1907  
**Repository:** https://github.com/Rohit-1907/cloudvault  
**Tech Stack:** Java 21, Spring Boot 3.1.5, PostgreSQL, Maven, AES-256 Encryption
