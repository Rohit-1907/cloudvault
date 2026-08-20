# 📦 CloudVault - Team Handover

## What You're Getting

A **production-ready** cloud file storage system with:

✅ File upload with AES-256 encryption  
✅ Automatic file expiry and deletion  
✅ Temporary share links for public access  
✅ View-only and downloadable modes  
✅ Cloud database (PostgreSQL)  
✅ Clean, simple web interface  
✅ No authentication required  

## Files Included

```
cloudvault/
├── README.md              # Full documentation
├── QUICKSTART.md          # 5-minute setup guide
├── pom.xml                # Maven dependencies
├── .gitignore             # Git configuration
├── src/
│   └── main/
│       ├── java/com/cloudvault/  # Java source code
│       │   ├── controller/       # REST API endpoints
│       │   ├── service/          # Business logic
│       │   ├── model/            # Database entities
│       │   ├── repository/       # Data access
│       │   └── util/             # Utilities
│       └── resources/            # Configuration & frontend
│           ├── static/           # HTML, CSS, JS
│           └── application.properties
└── cloudvault-storage/  # Uploaded files (created at runtime)
```

## Technology Stack

| Component | Technology |
|-----------|------------|
| Backend | Java 17, Spring Boot 3.1.5 |
| Database | PostgreSQL (via Supabase) |
| Storage | Local filesystem |
| Encryption | AES-256-CBC |
| Frontend | HTML, CSS, Vanilla JavaScript |
| Build | Maven |

## What to Do First

1. **Read**: `QUICKSTART.md` for 5-minute setup
2. **Configure**: Database connection in `application.properties`
3. **Build**: `mvn clean install`
4. **Run**: `mvn spring-boot:run`
5. **Test**: http://localhost:8080

## Key Features to Know

### File Upload
- Supports any file type
- Max 50MB (configurable)
- Encrypted before storage
- Unique token generated per file

### Expiry Options
- 5 minutes
- 1 hour
- 6 hours
- 24 hours
- 7 days

### Access Modes
- **Download & View**: Recipients can download and view
- **View Only**: Recipients can only view in browser

### Share Link Format
```
http://localhost:8080/share.html?token=XXXXXXXXXXXXXXXX
```

## Database Schema

Only one table needed:

```sql
CREATE TABLE cloud_files (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255),
    original_name VARCHAR(255),
    share_token CHAR(16) UNIQUE,
    file_size BIGINT,
    upload_date TIMESTAMP,
    expiry_date TIMESTAMP,
    access_mode VARCHAR(50),
    is_expired BOOLEAN,
    user_id BIGINT,
    download_count INT
);
```

## Configuration Options

Edit `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://HOST:5432/DB
spring.datasource.username=USER
spring.datasource.password=PASS

# Upload limits
spring.servlet.multipart.max-file-size=50MB

# Base URL for share links
app.base-url=http://localhost:8080

# Expiry check interval (milliseconds)
scheduler.file-expiry.interval=300000
```

## API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/files/upload` | Upload file |
| GET | `/api/files/access/{token}` | Get file metadata |
| GET | `/api/files/download/{token}` | Download encrypted file |
| GET | `/api/files/view/{token}` | View file (VIEW_ONLY mode) |

## Important Notes

### Security
- Files are encrypted with AES-256-CBC
- Each file gets a 16-char random token
- Share tokens are hard to guess
- No authentication needed (public by design)

### Storage
- Currently stores in local filesystem
- Can be easily configured for S3, Google Cloud Storage, etc.
- Storage directory: `cloudvault-storage/`

### Expiry
- Scheduled job runs every 5 minutes
- Checks for expired files
- Automatically deletes expired files
- Can be disabled/configured

## Deployment Checklist

- [ ] Use HTTPS (SSL certificate)
- [ ] Configure strong database password
- [ ] Setup database backups
- [ ] Configure cloud storage instead of local filesystem
- [ ] Setup monitoring/logging
- [ ] Configure proper CORS headers
- [ ] Setup rate limiting
- [ ] Enable database connection pooling
- [ ] Configure max file sizes
- [ ] Setup CDN for static files

## Support & Documentation

- **Full docs**: See `README.md`
- **Quick setup**: See `QUICKSTART.md`
- **Code comments**: Check source code for detailed comments
- **Database**: Fully documented schema

## Version Info

- **Version**: 1.0.0
- **Java**: 17+
- **Spring Boot**: 3.1.5
- **Maven**: 3.6+
- **PostgreSQL**: 12+

## Next Steps for Team

1. ✅ Review README.md
2. ✅ Follow QUICKSTART.md
3. ✅ Test locally
4. ✅ Deploy to staging
5. ✅ Configure for production
6. ✅ Setup monitoring
7. ✅ Go live! 🎉

---

**Ready to deploy?** Start with `QUICKSTART.md`!
