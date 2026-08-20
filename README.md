# ☁️ CloudVault - Secure Self-Expiring Cloud File Storage

A simple and secure file upload, storage, and sharing system with automatic expiry.

## 🚀 Features

- **File Upload**: Upload files with AES-256 encryption
- **Auto Expiry**: Files automatically expire after user-selected duration
- **Secure Sharing**: Generate temporary share links for public access
- **Public Viewer**: Recipients can view files without authentication
- **Multiple Access Modes**: 
  - Download & View (recipients can download)
  - View Only (recipients cannot download)
- **Cloud Storage**: Files stored securely in Supabase
- **No Authentication**: Simple public interface, no login required

## 📋 Tech Stack

- **Backend**: Java + Spring Boot 3.1.5
- **Database**: PostgreSQL (via Supabase)
- **Storage**: Local filesystem (easily swap to cloud storage)
- **Encryption**: AES-256-CBC
- **Frontend**: HTML + CSS + JavaScript (Vanilla)

## 🛠️ Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (Supabase free tier works great)

## ⚙️ Setup

### 1. Database Setup (Supabase)

Create a Supabase PostgreSQL database. Run this SQL:

```sql
CREATE TABLE IF NOT EXISTS cloud_files (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    share_token CHAR(16) UNIQUE NOT NULL,
    file_size BIGINT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP NOT NULL,
    access_mode VARCHAR(50),
    is_expired BOOLEAN DEFAULT false,
    user_id BIGINT,
    download_count INT DEFAULT 0
);

CREATE INDEX idx_share_token ON cloud_files(share_token);
CREATE INDEX idx_expiry_date ON cloud_files(expiry_date);
```

### 2. Configure Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/YOUR_DB
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
```

### 3. Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

App will be available at: **http://localhost:8080**

## 📖 Usage

### Upload File

1. Go to main page
2. Select a file
3. Choose expiry time (5 min, 1 hour, 6 hours, 24 hours, 7 days)
4. Choose access mode (Download & View or View Only)
5. Click "Upload File"
6. Copy the share link and share with others

### Access Shared File

1. Open the share link (or go to `/share.html?token=XXXXX`)
2. View file info
3. Click "Download File" to download (if allowed)
4. Or just view in browser

## 🔐 Security

- **Encryption**: AES-256-CBC encryption of uploaded files
- **Tokens**: 16-character random tokens for share links
- **Expiry**: Automatic file deletion after expiry
- **Storage**: Local storage (can be configured to use cloud storage)

## 📁 Project Structure

```
cloudvault/
├── src/main/java/com/cloudvault/
│   ├── controller/          # REST endpoints
│   ├── service/             # Business logic
│   ├── model/               # Database entities
│   ├── repository/          # Data access layer
│   ├── util/                # Utilities
│   └── CloudVaultApplication.java
├── src/main/resources/
│   ├── static/              # HTML, CSS, JS
│   └── application.properties
├── pom.xml                  # Dependencies
└── cloudvault-storage/      # Uploaded files directory
```

## 🧪 Testing

1. **Upload a file**
   - Select file
   - Choose "1 hour" expiry
   - Choose "Download & View"
   - Click Upload

2. **Share the link**
   - Copy the share link
   - Open in new browser/incognito
   - Should see file info and download button

3. **View Only Mode**
   - Upload with "View Only" access
   - Shared link should allow viewing only
   - No download button should appear

## 📝 Configuration

### File Upload Limits

Edit `application.properties`:

```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

### Expiry Check Interval

```properties
scheduler.file-expiry.interval=300000  # 5 minutes in milliseconds
```

### Base URL (for share links)

```properties
app.base-url=http://localhost:8080
```

## 🐛 Troubleshooting

### Files not deleting after expiry
- Check that expiry scheduler is running
- Check database connectivity
- Check file permissions in storage directory

### Upload fails
- Check max file size limits
- Verify database connection
- Check storage directory permissions
- Check disk space

### Share links not working
- Verify token is correct
- Check if file has expired
- Check database has cloud_files table

## 📚 API Endpoints

- `POST /api/files/upload` - Upload file
- `GET /api/files/access/{token}` - Get file info
- `GET /api/files/download/{token}` - Download file
- `GET /api/files/view/{token}` - View file (for VIEW_ONLY mode)

## 🚀 Deployment

For production deployment:

1. Use HTTPS only
2. Configure CORS properly
3. Use managed cloud storage (S3, Google Cloud Storage, etc.)
4. Enable database backups
5. Set up monitoring and logging
6. Use environment variables for secrets

## 📞 Support

For issues or questions, check the logs:

```bash
tail -f /path/to/cloudvault.log
```

---

**Version**: 1.0.0  
**License**: MIT  
**Last Updated**: August 2026
