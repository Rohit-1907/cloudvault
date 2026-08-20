# 🔒 CloudVault - Secure File Storage System

CloudVault is a secure, temporary file storage and sharing system with end-to-end encryption, built with Spring Boot and Supabase.

## ✨ Features

- **End-to-End Encryption**: All files are encrypted using AES-256 before storage
- **Temporary File Sharing**: Files automatically expire after a set duration (5 min, 1 hour, 24 hours, 7 days)
- **Two Access Modes**: 
  - Download & View: Recipients can download and view files
  - View Only: Recipients can only view files in browser (no download)
- **Secure Share Links**: Each file gets a unique, secure share token
- **Auto IP Detection**: Automatically detects your network IP for sharing
- **Database Integration**: PostgreSQL database via Supabase
- **Scheduled Cleanup**: Automatic deletion of expired files

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Supabase account (free tier works)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/cloudvault.git
   cd cloudvault
   ```

2. **Set up configuration**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

3. **Configure your settings** in `application.properties`:
   - Add your Supabase database URL
   - Add your Supabase API key
   - Set a strong encryption master key
   - (Optional) Set custom base URL or leave as localhost for auto-detection

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Local: http://localhost:8080
   - Network: http://YOUR_IP:8080

## ⚙️ Configuration

### Database Setup (Supabase)

1. Create a free Supabase project at https://supabase.com
2. Get your database connection string and API keys from Project Settings
3. Update `application.properties` with your credentials

The application will automatically create the required `cloud_files` table.

### Environment Variables (Production)

For production deployment, use environment variables instead of properties file:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-host:5432/postgres
SPRING_DATASOURCE_PASSWORD=your-password
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_KEY=your-anon-key
ENCRYPTION_MASTER_KEY=your-secure-key
APP_BASE_URL=https://yourdomain.com
```

## 🔐 Security Features

1. **File Encryption**: AES-256-CBC encryption for all stored files
2. **Secure Tokens**: UUID-based share tokens (128-bit randomness)
3. **Automatic Expiry**: Files auto-delete after expiration
4. **Access Control**: View-only mode prevents unauthorized downloads
5. **Database Encryption**: Sensitive metadata stored securely

## 📁 Project Structure

```
cloudvault/
├── src/main/java/com/cloudvault/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST API endpoints
│   ├── dto/            # Data transfer objects
│   ├── model/          # Entity models
│   ├── repository/     # Database repositories
│   ├── service/        # Business logic
│   └── util/           # Utility classes
├── src/main/resources/
│   ├── static/         # Frontend (HTML/CSS/JS)
│   └── application.properties.example
└── cloudvault-storage/ # Encrypted file storage (gitignored)
```

## 🌐 API Endpoints

- `POST /api/files/upload` - Upload a file
- `GET /api/files/access/{token}` - Get file information
- `GET /api/files/download/{token}` - Download a file
- `GET /api/files/view/{token}` - View file in browser
- `GET /api/files/verify/{token}` - Verify encryption
- `GET /file/{token}` - Public share page

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.1.5, Java 21
- **Database**: PostgreSQL (via Supabase)
- **Security**: AES-256 encryption
- **Storage**: Local encrypted file system
- **Frontend**: Vanilla HTML/CSS/JavaScript

## 📝 Important Notes

### Before Pushing to GitHub

1. **Never commit `application.properties`** with real credentials
2. The `.gitignore` already excludes sensitive files
3. Use `application.properties.example` as a template
4. Set real secrets via environment variables in production

### IP Address & Networking

- The app auto-detects your local IP address
- Share links work on the same network (WiFi/hotspot)
- For internet access, deploy to a cloud platform (Heroku, Railway, etc.)
- If IP changes, restart the application to detect new IP

### File Storage

- Files are stored encrypted in `cloudvault-storage/uploads/`
- This directory is gitignored (contains user data)
- Expired files are automatically cleaned every 5 minutes

## 🚀 Deployment

### Deploy to Railway/Render/Heroku

1. Set all environment variables in your platform
2. Ensure PostgreSQL database is accessible
3. Set `app.base-url` to your deployment URL
4. Deploy from GitHub

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## ⚠️ Disclaimer

CloudVault is designed for temporary file sharing. For production use:
- Use strong encryption keys
- Enable HTTPS
- Implement user authentication
- Add rate limiting
- Regular security audits

## 🐛 Troubleshooting

### Share links not working
- Check if application is running
- Verify IP address is correct (check console logs)
- Ensure recipient is on same network

### Database connection failed
- Verify Supabase credentials
- Check database URL format
- Ensure Supabase project is active

### Files not expiring
- Check scheduler configuration
- Verify system clock is correct
- Check application logs for errors

## 📞 Support

For issues and questions:
- Open an issue on GitHub
- Check existing issues for solutions

---

Made with ❤️ for secure file sharing
