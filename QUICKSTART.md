# 🚀 Quick Start - CloudVault

Get the app running in 5 minutes.

## Step 1: Setup Database

Use Supabase free tier:

1. Go to https://supabase.com/dashboard
2. Create a new project
3. Get your connection details
4. Create the `cloud_files` table (see README.md)

## Step 2: Configure Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/YOUR_DB
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
app.base-url=http://localhost:8080  # Change if deploying elsewhere
```

## Step 3: Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

## Step 4: Test

1. Open http://localhost:8080
2. Upload a test file
3. Choose expiry and access mode
4. Copy share link
5. Open in new browser - should work! ✅

## 🎯 Next Steps

- [ ] Deploy to production server
- [ ] Setup HTTPS with SSL certificate
- [ ] Configure cloud storage (S3, GCS, etc.)
- [ ] Setup database backups
- [ ] Configure monitoring/logging
- [ ] Update CORS settings for your domain

## 🆘 Issues?

Check these common problems:

1. **"Database connection failed"**
   - Verify credentials in application.properties
   - Check PostgreSQL is running and accessible

2. **"Upload fails"**
   - Check cloudvault-storage/ directory exists
   - Verify write permissions
   - Check disk space

3. **"Share link doesn't work"**
   - Verify app.base-url is correct
   - Check file hasn't expired
   - Check database has cloud_files table

---

Happy uploading! 🎉
