# 🏗️ CloudVault - Architecture & Flow Diagrams

## 📊 System Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    CLIENT (Web Browser)                       │
│  ┌────────────┐  ┌────────────┐  ┌────────────────────────┐ │
│  │ index.html │  │ share.html │  │  script.js + style.css │ │
│  └────────────┘  └────────────┘  └────────────────────────┘ │
└──────────────────────────┬───────────────────────────────────┘
                           │ HTTP/HTTPS
                           │ (REST API Calls)
                           ▼
┌──────────────────────────────────────────────────────────────┐
│              SPRING BOOT APPLICATION (Port 8080)             │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │              CONTROLLER LAYER                            │ │
│ │  ┌─────────────────┐  ┌──────────────────────────────┐  │ │
│ │  │ FileController  │  │ PublicShareController        │  │ │
│ │  │ /api/files/*    │  │ /file/{token}, /share        │  │ │
│ │  └────────┬────────┘  └──────────────────────────────┘  │ │
│ └───────────┼─────────────────────────────────────────────┘ │
│             │                                                 │
│ ┌───────────▼─────────────────────────────────────────────┐ │
│ │              SERVICE LAYER (Business Logic)             │ │
│ │  ┌──────────────┐  ┌──────────────────┐  ┌───────────┐│ │
│ │  │ FileService  │  │EncryptionService │  │CloudStorage││ │
│ │  └──────┬───────┘  └────────┬─────────┘  └─────┬─────┘│ │
│ └─────────┼────────────────────┼──────────────────┼──────┘ │
│           │                    │                  │         │
│ ┌─────────▼────────────────────▼──────────────────▼──────┐ │
│ │              REPOSITORY LAYER (Data Access)            │ │
│ │  ┌──────────────────────────────────────────────────┐  │ │
│ │  │  CloudFileRepository (Spring Data JPA)          │  │ │
│ │  └───────────────────┬──────────────────────────────┘  │ │
│ └──────────────────────┼─────────────────────────────────┘ │
└────────────────────────┼───────────────────────────────────┘
                         │
           ┌─────────────┴──────────────┐
           │                            │
           ▼                            ▼
┌──────────────────────┐    ┌──────────────────────────┐
│   PostgreSQL DB      │    │   Local File System      │
│   (Supabase)         │    │   cloudvault-storage/    │
│                      │    │                          │
│ Tables:              │    │ Encrypted Files:         │
│ - cloud_files        │    │ - {uuid}.pdf             │
│                      │    │ - {uuid}.jpg             │
│ Stores:              │    │ - {uuid}.txt             │
│ - File metadata      │    │                          │
│ - Share tokens       │    │                          │
│ - Expiry dates       │    │                          │
└──────────────────────┘    └──────────────────────────┘
```

---

## 🔄 File Upload Flow

```
Step 1: User Action
┌─────────────┐
│   Browser   │
│  (User)     │
└──────┬──────┘
       │ Selects file, expiry time
       │ Clicks "Upload"
       ▼
┌──────────────────┐
│  Frontend JS     │
│  (script.js)     │
└──────┬───────────┘
       │ POST /api/files/upload
       │ FormData: file, expiry, accessMode
       ▼

Step 2: Controller receives request
┌──────────────────────────┐
│  FileController.java     │
│  @PostMapping("/upload") │
└──────┬───────────────────┘
       │ uploadFile(MultipartFile, expiry, mode)
       ▼

Step 3: Service processes file
┌────────────────────────────────────┐
│  FileService.java                  │
│                                    │
│  1. Generate UUID token            │
│  2. Calculate expiry date          │
│  3. ─→ EncryptionService.encrypt() │
│  4. ─→ CloudStorageService.save()  │
│  5. ─→ Repository.save()           │
│  6. Generate share link            │
└────────┬───────────────────────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼

┌──────────────────┐  ┌─────────────────────┐
│EncryptionService │  │ CloudStorageService │
│                  │  │                     │
│ AES-256 Encrypt  │  │ Save to disk        │
│ Returns bytes    │  │ /uploads/{uuid}.ext │
└──────────────────┘  └─────────────────────┘
         │
         ▼
┌───────────────────────┐
│ CloudFileRepository   │
│ (JPA)                 │
│                       │
│ INSERT INTO           │
│ cloud_files VALUES    │
│ (id, name, token,     │
│  expiry, size...)     │
└───────────────────────┘
         │
         ▼
┌───────────────────────┐
│  PostgreSQL Database  │
│  (Supabase)           │
└───────────────────────┘
         │
         ▼

Step 4: Return response
┌─────────────────────────┐
│  FileUploadResponse     │
│                         │
│  {                      │
│    shareLink: "http://..│
│    fileName: "doc.pdf"  │
│    fileSize: 1024       │
│    expiryDate: "..."    │
│  }                      │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────┐
│  Browser        │
│  Shows success  │
│  + share link   │
└─────────────────┘
```

---

## 🔗 Share Link Access Flow

```
Step 1: User clicks share link
┌─────────────────────────────────┐
│  http://IP:8080/file/abc123...  │
└────────────┬────────────────────┘
             │
             ▼

Step 2: Controller receives request
┌─────────────────────────────────┐
│ PublicShareController.java      │
│ @GetMapping("/file/{token}")    │
│                                 │
│ redirect:/share.html?token=abc  │
└────────────┬────────────────────┘
             │
             ▼

Step 3: Browser loads share page
┌─────────────────────────────────┐
│  share.html                     │
│  + script.js                    │
│                                 │
│  Extracts token from URL        │
└────────────┬────────────────────┘
             │ GET /api/files/access/{token}
             ▼

Step 4: Fetch file info
┌─────────────────────────────────┐
│  FileController.java            │
│  @GetMapping("/access/{token}") │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  FileService.java               │
│                                 │
│  1. Find by token in DB         │
│  2. Check if expired            │
│  3. Return file info            │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  CloudFileRepository            │
│  findByShareToken(token)        │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  FileAccessResponse             │
│  {                              │
│    fileName: "document.pdf"     │
│    fileSize: 2048               │
│    expiryDate: "2026-08-21"     │
│    accessMode: "DOWNLOAD_VIEW"  │
│  }                              │
└────────────┬────────────────────┘
             │
             ▼

Step 5: Display to user
┌─────────────────────────────────┐
│  Browser shows:                 │
│  - File name                    │
│  - File size                    │
│  - Expiry time                  │
│  - Download button              │
└─────────────────────────────────┘
```

---

## 🔐 Encryption Flow

```
UPLOAD (Encryption):
┌──────────────┐
│ Original File│
│ "Hello.txt"  │
│ Content:     │
│ "Hello!"     │
└──────┬───────┘
       │
       ▼
┌────────────────────────────┐
│ EncryptionService.java     │
│                            │
│ Algorithm: AES-256-CBC     │
│ Key: masterEncryptionKey   │
│ IV: Random 16 bytes        │
└──────┬─────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ Encrypted Bytes              │
│ [0x4F, 0x2A, 0x9C, 0x3E...] │
│ (Unreadable without key)     │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ Saved to Disk                │
│ /uploads/uuid-timestamp.txt  │
└──────────────────────────────┘


DOWNLOAD (Decryption):
┌──────────────────────────────┐
│ Encrypted File from Disk     │
│ [0x4F, 0x2A, 0x9C, 0x3E...] │
└──────┬───────────────────────┘
       │
       ▼
┌────────────────────────────┐
│ EncryptionService.java     │
│                            │
│ Decrypt with same key      │
│ Algorithm: AES-256-CBC     │
└──────┬─────────────────────┘
       │
       ▼
┌──────────────┐
│ Original File│
│ "Hello.txt"  │
│ Content:     │
│ "Hello!"     │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ User Download│
└──────────────┘
```

---

## ⏰ Scheduled Cleanup Flow

```
Every 5 minutes:

┌─────────────────────────────────┐
│ FileExpirySchedulerService      │
│ @Scheduled(fixedRate = 300000)  │
│                                 │
│ cleanupExpiredFiles()           │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│ Query Database                  │
│ SELECT * FROM cloud_files       │
│ WHERE expiry_date < NOW()       │
└────────────┬────────────────────┘
             │
             ▼
        Found expired?
             │
      ┌──────┴──────┐
      │ No          │ Yes
      ▼             ▼
   [Done]    ┌─────────────────┐
             │ For each file:  │
             │ 1. Delete disk  │
             │ 2. Delete DB    │
             └─────────────────┘
```

---

## 🗄️ Database Schema

```sql
CREATE TABLE cloud_files (
    id BIGSERIAL PRIMARY KEY,
    
    -- File information
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    file_size BIGINT,
    
    -- Sharing
    share_token VARCHAR(255) UNIQUE NOT NULL,
    access_mode VARCHAR(50),
    
    -- Timing
    upload_date TIMESTAMP DEFAULT NOW(),
    expiry_date TIMESTAMP NOT NULL,
    
    -- Status
    file_status VARCHAR(50),
    is_encrypted BOOLEAN DEFAULT true,
    
    -- Tracking
    download_count INT DEFAULT 0,
    
    -- Indexes
    INDEX idx_share_token (share_token),
    INDEX idx_expiry_date (expiry_date)
);
```

**Sample Data:**
```
id  | file_name             | share_token | expiry_date         | access_mode
----|-----------------------|-------------|---------------------|-------------
1   | abc-123.pdf          | xyz789abc   | 2026-08-21 10:00:00 | DOWNLOAD_VIEW
2   | def-456.jpg          | qwe456def   | 2026-08-20 15:30:00 | VIEW_ONLY
```

---

## 🌐 API Endpoints Map

```
PUBLIC ENDPOINTS (No Auth):
│
├── GET  /                          → index.html (Upload page)
├── GET  /share.html                → share.html (Share page)
├── GET  /file/{token}              → Redirect to share page
│
API ENDPOINTS:
│
├── POST   /api/files/upload        → Upload file
│   Request: MultipartFile, expiry, accessMode
│   Response: FileUploadResponse (share link)
│
├── GET    /api/files/access/{token}  → Get file info
│   Response: FileAccessResponse
│
├── GET    /api/files/download/{token} → Download file
│   Response: File bytes (decrypted)
│
├── GET    /api/files/view/{token}    → View file in browser
│   Response: File bytes (decrypted)
│
└── GET    /api/files/verify/{token}  → Verify encryption
    Response: EncryptionVerificationResponse
```

---

## 🔧 Configuration Flow

```
application.properties
       │
       ├─→ Database Config
       │   ├─ spring.datasource.url
       │   ├─ spring.datasource.username
       │   └─ spring.datasource.password
       │
       ├─→ JPA Config
       │   ├─ spring.jpa.hibernate.ddl-auto=create
       │   └─ spring.jpa.database-platform=PostgreSQL
       │
       ├─→ Supabase Config
       │   ├─ supabase.url
       │   └─ supabase.key
       │
       ├─→ Encryption Config
       │   ├─ encryption.algorithm=AES
       │   ├─ encryption.key-size=256
       │   └─ encryption.master-key=...
       │
       └─→ App Config
           ├─ server.port=8080
           └─ app.base-url=http://localhost:8080
```

---

## 📊 Technology Stack Visual

```
┌─────────────────────────────────────────────┐
│           PRESENTATION LAYER                │
│   HTML5 + CSS3 + Vanilla JavaScript        │
└─────────────────┬───────────────────────────┘
                  │ HTTP/REST
┌─────────────────▼───────────────────────────┐
│          APPLICATION LAYER                  │
│  ┌────────────────────────────────────┐    │
│  │      Spring Boot 3.1.5             │    │
│  │  ┌──────────────────────────────┐  │    │
│  │  │  Spring Web (REST API)       │  │    │
│  │  │  Spring Data JPA (ORM)       │  │    │
│  │  │  Spring Scheduler            │  │    │
│  │  └──────────────────────────────┘  │    │
│  └────────────────────────────────────┘    │
│                                             │
│  ┌────────────────────────────────────┐    │
│  │    Business Logic (Services)       │    │
│  │  - File Processing                 │    │
│  │  - Encryption (AES-256)            │    │
│  │  - Share Link Generation           │    │
│  └────────────────────────────────────┘    │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│          PERSISTENCE LAYER                  │
│  ┌────────────────┐  ┌──────────────────┐  │
│  │ Hibernate/JPA  │  │ File System      │  │
│  │ (ORM)          │  │ Storage          │  │
│  └────────┬───────┘  └──────────────────┘  │
└───────────┼─────────────────────────────────┘
            │
┌───────────▼─────────────────────────────────┐
│          DATA LAYER                         │
│  PostgreSQL Database (Supabase)             │
└─────────────────────────────────────────────┘

SUPPORTING TOOLS:
┌─────────────────────────────────────────────┐
│  Maven - Build & Dependency Management      │
│  Lombok - Code Generation                   │
│  HikariCP - Connection Pooling              │
└─────────────────────────────────────────────┘
```

---

This diagram shows the complete architecture and data flow of CloudVault!
