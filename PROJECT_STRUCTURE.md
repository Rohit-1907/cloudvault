# 📁 CloudVault - Complete Project Structure

**Repository:** https://github.com/Rohit-1907/cloudvault  
**Tech Stack:** Java 21, Spring Boot 3.1.5, PostgreSQL, Maven, AES-256 Encryption

---

## 📋 1. Simplified Structure

```
CloudVault Project
│
├── 📦 Backend (Java + Spring Boot)
│   ├── 🎯 Main Application Entry Point
│   ├── 🌐 Controllers (REST API Layer)
│   ├── ⚙️ Services (Business Logic Layer)
│   ├── 🗄️ Repositories (Database Layer)
│   ├── 📦 Models (Entity/Data Layer)
│   ├── 📤 DTOs (Data Transfer Objects)
│   ├── 🛠️ Utilities (Helper Classes)
│   └── 🚨 Exception Handlers
│
├── 🎨 Frontend (HTML/CSS/JavaScript)
│   ├── Upload Page (index.html)
│   ├── Share Page (share.html)
│   ├── JavaScript Logic (script.js)
│   └── Styling (style.css)
│
├── 🔧 Configuration & Build
│   ├── Maven (pom.xml)
│   ├── Application Settings (application.properties)
│   ├── Git Configuration (.gitignore)
│   └── Deployment Config (Procfile)
│
├── 💾 Storage (Runtime - Not in Git)
│   └── Encrypted Files Directory
│
├── 📦 Build Output (Generated - Not in Git)
│   └── Compiled Classes & JAR file
│
└── 📚 Documentation
    ├── README & Setup Guides
    ├── Technical Explanations
    └── Architecture Diagrams
```

---

## 🌳 2. Visual Tree (Detailed)

```
cloudvault/
│
├── 🔧 PROJECT ROOT FILES
│   ├── 📄 pom.xml                              Maven: Dependencies & Build Config
│   ├── 📄 .gitignore                           Git: Ignore sensitive/generated files
│   ├── 📄 Procfile                             Deployment configuration
│   ├── 📄 README.md                            Project overview
│   ├── 📄 PROJECT_EXPLANATION.md               Complete tech stack explanation
│   ├── 📄 ARCHITECTURE_DIAGRAM.md              System architecture & flows
│   ├── 📄 SETUP.md                             Setup & GitHub guide
│   └── 📄 start-cloudvault.ps1                 Windows startup script
│
├── 📂 src/main/java/com/cloudvault/           BACKEND SOURCE CODE
│   │
│   ├── 🎯 CloudVaultApplication.java           ⭐ Main Entry Point
│   │                                           @SpringBootApplication
│   │                                           Starts embedded Tomcat server
│   │
│   ├── 📂 config/                              CONFIGURATION LAYER
│   │   └── 📄 SupabaseConfig.java              Database connection config
│   │
│   ├── 📂 controller/                          🌐 REST API LAYER (HTTP Endpoints)
│   │   ├── 📄 FileController.java              ⭐ Main API Controller
│   │   │                                       POST /api/files/upload
│   │   │                                       GET /api/files/access/{token}
│   │   │                                       GET /api/files/download/{token}
│   │   │                                       GET /api/files/view/{token}
│   │   │
│   │   ├── 📄 PublicShareController.java       Share link routing
│   │   │                                       GET /file/{token}
│   │   │                                       Redirects to share.html
│   │   │
│   │   └── 📄 AdminController.java             Admin endpoints
│   │                                           File management APIs
│   │
│   ├── 📂 service/                             ⚙️ BUSINESS LOGIC LAYER
│   │   ├── 📄 FileService.java                 ⭐ Core File Operations
│   │   │                                       - Upload file
│   │   │                                       - Generate share token
│   │   │                                       - Create share link
│   │   │                                       - Validate access
│   │   │                                       - Download/view files
│   │   │
│   │   ├── 📄 EncryptionService.java           ⭐ Security Layer
│   │   │                                       - AES-256-CBC encryption
│   │   │                                       - Encrypt files before storage
│   │   │                                       - Decrypt files on download
│   │   │
│   │   ├── 📄 CloudStorageService.java         File System Operations
│   │   │                                       - Save encrypted files
│   │   │                                       - Read files from disk
│   │   │                                       - Delete files
│   │   │
│   │   └── 📄 FileExpirySchedulerService.java  ⏰ Scheduled Tasks
│   │                                           @Scheduled (every 5 min)
│   │                                           Auto-delete expired files
│   │
│   ├── 📂 repository/                          🗄️ DATABASE ACCESS LAYER
│   │   └── 📄 CloudFileRepository.java         ⭐ JPA Repository
│   │                                           extends JpaRepository
│   │                                           CRUD operations on DB
│   │                                           Custom queries
│   │
│   ├── 📂 model/                               📦 ENTITY/DATA MODELS
│   │   ├── 📄 CloudFile.java                   ⭐ Main Entity
│   │   │                                       @Entity (maps to DB table)
│   │   │                                       Fields: id, fileName, shareToken,
│   │   │                                       uploadDate, expiryDate, fileSize
│   │   │
│   │   ├── 📄 FileStatus.java                  Enum: ACTIVE, EXPIRED, DELETED
│   │   │
│   │   └── 📄 AccessMode.java                  Enum: DOWNLOAD_VIEW, VIEW_ONLY
│   │
│   ├── 📂 dto/                                 📤 DATA TRANSFER OBJECTS
│   │   ├── 📄 FileUploadRequest.java           Upload API request structure
│   │   ├── 📄 FileUploadResponse.java          Upload API response (share link)
│   │   ├── 📄 FileAccessResponse.java          File info response
│   │   └── 📄 EncryptionVerificationResponse.java
│   │
│   ├── 📂 exception/                           🚨 ERROR HANDLING
│   │   └── 📄 GlobalExceptionHandler.java      @ControllerAdvice
│   │                                           Catches & handles all exceptions
│   │
│   └── 📂 util/                                🛠️ UTILITY CLASSES
│       ├── 📄 NetworkUtils.java                Auto-detect local IP address
│       └── 📄 SupabaseConnectionValidator.java Database health check
│
├── 📂 src/main/resources/                     RESOURCES & FRONTEND
│   │
│   ├── 📄 application.properties               ⚠️ Configuration (gitignored)
│   │                                           Database credentials
│   │                                           Encryption keys
│   │                                           App settings
│   │
│   ├── 📄 application.properties.example       Template for teammates
│   │                                           Safe to commit (no secrets)
│   │
│   └── 📂 static/                              🎨 FRONTEND FILES
│       ├── 📄 index.html                       ⭐ Upload Page
│       │                                       File selection UI
│       │                                       Expiry time options
│       │                                       Access mode selection
│       │
│       ├── 📄 share.html                       ⭐ Share/Download Page
│       │                                       Display file info
│       │                                       Download button
│       │                                       View file option
│       │
│       ├── 📄 script.js                        ⭐ Frontend Logic
│       │                                       AJAX calls to backend
│       │                                       File upload handling
│       │                                       Token extraction from URL
│       │                                       Dynamic UI updates
│       │
│       └── 📄 style.css                        UI Styling
│                                               Responsive design
│                                               Modern look & feel
│
├── 📂 target/                                  ⚠️ BUILD OUTPUT (gitignored)
│   ├── 📂 classes/                             Compiled .class files
│   └── 📄 cloudvault-1.0.0.jar                 Executable JAR file
│                                               Run with: java -jar
│
└── 📂 cloudvault-storage/                      ⚠️ FILE STORAGE (gitignored)
    └── 📂 uploads/                             Encrypted user files
        ├── 📄 uuid-timestamp-123.pdf           Encrypted file (unreadable)
        ├── 📄 uuid-timestamp-456.jpg           Encrypted file (unreadable)
        └── 📄 uuid-timestamp-789.txt           Encrypted file (unreadable)
```

---

## 🔄 3. Component Interaction Diagram

### **High-Level System Architecture**

```
┌────────────────────────────────────────────────────────────────┐
│                    CLIENT BROWSER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │  index.html  │  │  share.html  │  │ script.js + CSS    │  │
│  │  (Upload)    │  │  (Download)  │  │ (Frontend Logic)   │  │
│  └──────┬───────┘  └──────┬───────┘  └─────────┬──────────┘  │
└─────────┼──────────────────┼────────────────────┼─────────────┘
          │                  │                    │
          │ HTTP POST        │ HTTP GET           │ AJAX
          │ /api/files/      │ /file/{token}      │ Requests
          │ upload           │                    │
          ▼                  ▼                    ▼
┌────────────────────────────────────────────────────────────────┐
│              SPRING BOOT APPLICATION (Port 8080)               │
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │           🌐 CONTROLLER LAYER (REST API)                 │ │
│  │  ┌────────────────────┐  ┌──────────────────────────┐   │ │
│  │  │ FileController     │  │ PublicShareController    │   │ │
│  │  │ @RestController    │  │ @Controller              │   │ │
│  │  │                    │  │                          │   │ │
│  │  │ uploadFile()       │  │ getFileSharePage()       │   │ │
│  │  │ getFileAccess()    │  │ (redirects to share.html)│   │ │
│  │  │ downloadFile()     │  │                          │   │ │
│  │  │ viewFile()         │  │                          │   │ │
│  │  └──────────┬─────────┘  └──────────────────────────┘   │ │
│  └─────────────┼────────────────────────────────────────────┘ │
│                │                                                │
│  ┌─────────────▼──────────────────────────────────────────┐   │
│  │           ⚙️ SERVICE LAYER (Business Logic)           │   │
│  │  ┌─────────────────┐  ┌──────────────────────────┐   │   │
│  │  │  FileService    │  │  EncryptionService       │   │   │
│  │  │  @Service       │  │  @Service                │   │   │
│  │  │                 │  │                          │   │   │
│  │  │ • Upload file   │  │ • encrypt(bytes)         │   │   │
│  │  │ • Generate      │  │ • decrypt(bytes)         │   │   │
│  │  │   share token   │  │ • AES-256-CBC            │   │   │
│  │  │ • Create share  │  │                          │   │   │
│  │  │   link          │  │                          │   │   │
│  │  │ • Validate      │  │                          │   │   │
│  │  │   access        │  │                          │   │   │
│  │  └────┬────────────┘  └────────┬─────────────────┘   │   │
│  │       │                        │                      │   │
│  │  ┌────▼────────────┐  ┌────────▼─────────────────┐  │   │
│  │  │CloudStorageServ │  │FileExpirySchedulerService│  │   │
│  │  │@Service         │  │@Service                  │  │   │
│  │  │                 │  │                          │  │   │
│  │  │ • Save to disk  │  │ @Scheduled(5 min)        │  │   │
│  │  │ • Read from disk│  │ • Find expired files     │  │   │
│  │  │ • Delete file   │  │ • Auto-delete            │  │   │
│  │  └────┬────────────┘  └──────────────────────────┘  │   │
│  └───────┼────────────────────────────────────────────┘    │
│          │                                                   │
│  ┌───────▼────────────────────────────────────────────┐    │
│  │           🗄️ REPOSITORY LAYER (Data Access)       │    │
│  │  ┌──────────────────────────────────────────────┐ │    │
│  │  │  CloudFileRepository                         │ │    │
│  │  │  extends JpaRepository                       │ │    │
│  │  │                                              │ │    │
│  │  │  • save()                                    │ │    │
│  │  │  • findByShareToken()                        │ │    │
│  │  │  • findByExpiryDateBefore()                  │ │    │
│  │  │  • delete()                                  │ │    │
│  │  └────────────────┬─────────────────────────────┘ │    │
│  └───────────────────┼───────────────────────────────┘    │
└────────────────────────┼─────────────────────────────────┘
                         │
          ┌──────────────┴──────────────┐
          │                             │
          ▼                             ▼
┌──────────────────────┐    ┌──────────────────────────┐
│  PostgreSQL Database │    │  Local File System       │
│  (Supabase Cloud)    │    │  cloudvault-storage/     │
│                      │    │                          │
│  Table: cloud_files  │    │  Encrypted Files:        │
│                      │    │  ├── {uuid}.pdf          │
│  Columns:            │    │  ├── {uuid}.jpg          │
│  • id               │    │  └── {uuid}.txt          │
│  • file_name        │    │                          │
│  • share_token      │    │  (AES-256 encrypted,     │
│  • upload_date      │    │   unreadable without key)│
│  • expiry_date      │    │                          │
│  • file_size        │    │                          │
│  • access_mode      │    │                          │
│  • file_status      │    │                          │
└──────────────────────┘    └──────────────────────────┘
```

---

## 🔀 4. Request Flow Diagram

### **File Upload Flow**

```
1. USER ACTION
   │
   ├─ User selects file
   ├─ Chooses expiry time (1 hour)
   ├─ Chooses access mode (Download & View)
   └─ Clicks "Upload"
   │
   ▼
2. FRONTEND (script.js)
   │
   ├─ Validate file
   ├─ Create FormData
   └─ POST /api/files/upload
   │
   ▼
3. CONTROLLER (FileController.java)
   │
   ├─ @PostMapping("/upload")
   ├─ Receive MultipartFile
   └─ Call FileService.uploadFile()
   │
   ▼
4. SERVICE (FileService.java)
   │
   ├─ Generate UUID token
   ├─ Calculate expiry date
   ├─ Call EncryptionService.encrypt()  ──────┐
   │                                           │
   ▼                                           ▼
5. ENCRYPTION (EncryptionService.java)        │
   │                                           │
   ├─ AES-256-CBC encryption                  │
   ├─ Use master key                          │
   └─ Return encrypted bytes ─────────────────┘
   │
   ▼
6. STORAGE (CloudStorageService.java)
   │
   ├─ Save encrypted file to disk
   └─ Path: uploads/{uuid}-{timestamp}.ext
   │
   ▼
7. DATABASE (CloudFileRepository.java)
   │
   ├─ Create CloudFile entity
   ├─ Set all fields (name, token, expiry)
   └─ repository.save()
   │
   ▼
8. RESPONSE
   │
   ├─ Generate share link: http://IP:8080/file/{token}
   ├─ Return FileUploadResponse
   └─ Display to user
```

### **File Download Flow**

```
1. USER CLICKS SHARE LINK
   │
   └─ http://192.168.1.8:8080/file/abc123xyz
   │
   ▼
2. CONTROLLER (PublicShareController.java)
   │
   ├─ @GetMapping("/file/{token}")
   └─ Redirect to /share.html?token=abc123xyz
   │
   ▼
3. FRONTEND (share.html)
   │
   ├─ Load page
   ├─ Extract token from URL
   └─ GET /api/files/access/{token}
   │
   ▼
4. CONTROLLER (FileController.java)
   │
   ├─ @GetMapping("/access/{token}")
   └─ Call FileService.getFileAccess()
   │
   ▼
5. SERVICE (FileService.java)
   │
   ├─ Find file by token in DB
   ├─ Check if expired
   ├─ Check access mode
   └─ Return FileAccessResponse
   │
   ▼
6. DISPLAY
   │
   ├─ Show file name
   ├─ Show file size
   ├─ Show expiry time
   └─ Show download button
   │
   ▼
7. USER CLICKS DOWNLOAD
   │
   └─ GET /api/files/download/{token}
   │
   ▼
8. DOWNLOAD PROCESS
   │
   ├─ FileService.downloadFile()
   ├─ Read encrypted file from disk
   ├─ EncryptionService.decrypt()
   └─ Return decrypted bytes
   │
   ▼
9. BROWSER
   │
   └─ Download file to user's computer
```

---

## 📊 5. Technology Stack Breakdown

```
┌─────────────────────────────────────────────────────────┐
│                  TECHNOLOGY LAYERS                      │
└─────────────────────────────────────────────────────────┘

PRESENTATION TIER
├─ HTML5              Frontend markup
├─ CSS3               Styling & layout
└─ JavaScript (ES6)   Client-side logic

APPLICATION TIER
├─ Java 21            Programming language
├─ Spring Boot 3.1.5  Application framework
│  ├─ Spring Web      REST API & MVC
│  ├─ Spring Data JPA Database abstraction
│  └─ Spring Scheduler Background tasks
├─ Maven 3.9.16       Build & dependency management
└─ Lombok             Reduce boilerplate code

DATA TIER
├─ PostgreSQL 17.6    Relational database
├─ Hibernate 6.2.13   ORM (Object-Relational Mapping)
├─ HikariCP 5.0.1     Connection pooling
└─ Supabase           Database hosting (cloud)

SECURITY LAYER
├─ AES-256-CBC        File encryption
└─ UUID               Secure token generation

DEPLOYMENT
├─ Tomcat 10.1.15     Embedded web server
├─ Railway            Cloud hosting (optional)
└─ GitHub             Version control
```

---

## 📈 6. File Statistics

```
┌────────────────────────────────────────────────┐
│          PROJECT METRICS                       │
├────────────────────────────────────────────────┤
│ Total Files:              37                   │
│ Java Files:               21                   │
│ Configuration Files:      5                    │
│ Frontend Files:           4                    │
│ Documentation Files:      7                    │
├────────────────────────────────────────────────┤
│ Lines of Code (approx):                        │
│   Backend (Java):         ~2,500 lines         │
│   Frontend (HTML/JS/CSS): ~800 lines           │
│   Configuration:          ~100 lines           │
│   Documentation:          ~1,500 lines         │
├────────────────────────────────────────────────┤
│ Package Size:                                  │
│   Source Code:            ~150 KB              │
│   Compiled JAR:           ~45 MB               │
│   Dependencies:           ~40 MB               │
└────────────────────────────────────────────────┘
```

---

## 🎯 7. Key Components Summary

| Component | File | Responsibility |
|-----------|------|----------------|
| **Entry Point** | CloudVaultApplication.java | Start Spring Boot app |
| **Upload API** | FileController.uploadFile() | Handle file uploads |
| **Encryption** | EncryptionService.encrypt() | AES-256 encryption |
| **Storage** | CloudStorageService.saveFile() | Save to disk |
| **Database** | CloudFileRepository.save() | Save metadata to DB |
| **Share Link** | FileService.generateShareLink() | Create shareable URL |
| **Download** | FileController.downloadFile() | Serve encrypted files |
| **Auto-Cleanup** | FileExpirySchedulerService | Delete expired files |
| **Frontend Upload** | index.html + script.js | User interface |
| **Frontend Share** | share.html | Download interface |

---

## 🔐 8. Security Features

```
┌─────────────────────────────────────────────────────────┐
│                  SECURITY LAYERS                        │
└─────────────────────────────────────────────────────────┘

FILE ENCRYPTION
├─ Algorithm: AES-256-CBC
├─ Key Size: 256 bits
├─ Mode: Cipher Block Chaining
└─ Master Key: Stored in application.properties

TOKEN SECURITY
├─ Generation: UUID (128-bit randomness)
├─ Uniqueness: Database constraint
└─ Unpredictable: Cryptographically secure

ACCESS CONTROL
├─ View Only Mode: Prevents downloads
├─ Download & View Mode: Full access
└─ Validation: Token-based access

DATA PROTECTION
├─ Encrypted at Rest: Files on disk encrypted
├─ Secure in Transit: HTTPS (in production)
├─ Auto-Expiry: Files deleted automatically
└─ No Authentication: Stateless token system

DATABASE SECURITY
├─ Connection Pool: HikariCP (prevents leaks)
├─ Prepared Statements: Prevents SQL injection
└─ Cloud Hosted: Supabase (managed security)
```

---

## 📞 Quick Reference

**GitHub Repository:** https://github.com/Rohit-1907/cloudvault

**Local Access:**
- Upload: http://localhost:8080
- Share: http://localhost:8080/file/{token}

**API Endpoints:**
- POST /api/files/upload
- GET /api/files/access/{token}
- GET /api/files/download/{token}
- GET /api/files/view/{token}

**Key Technologies:**
- Backend: Java 21 + Spring Boot 3.1.5
- Database: PostgreSQL (Supabase)
- Build: Maven 3.9.16
- Encryption: AES-256-CBC
- Frontend: HTML5, CSS3, JavaScript

---

**Created by:** Rohit-1907  
**Last Updated:** 2026  
**Version:** 1.0.0
