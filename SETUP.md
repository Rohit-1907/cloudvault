# 🛠️ CloudVault Setup Guide

This guide will help you set up CloudVault on your local machine and prepare it for GitHub.

## 📋 Before Pushing to GitHub

### ⚠️ IMPORTANT: Protect Your Credentials

Your `application.properties` file contains sensitive information like:
- Database passwords
- Supabase API keys
- Encryption keys

**These should NEVER be committed to GitHub!**

### ✅ What's Already Protected

The `.gitignore` file already excludes:
- `src/main/resources/application.properties` (your actual config)
- `cloudvault-storage/` (uploaded files)
- `target/` (compiled code)

### 📝 What TO Commit

- `application.properties.example` (template without secrets)
- All source code
- README and documentation
- `.gitignore` file

## 🚀 Setup Steps for New Developers

If someone clones your repository from GitHub, they should:

1. **Copy the example config:**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. **Edit `application.properties` with their own credentials:**
   - Supabase database URL
   - Supabase API key
   - Strong encryption key

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

## 🌐 IP Address Auto-Detection

### How It Works

CloudVault now automatically detects your local IP address!

- **In application.properties:** Set `app.base-url=http://localhost:8080`
- **The app will:** Auto-detect your actual IP (like `10.220.117.150`)
- **Share links will:** Use the detected IP automatically

### Why This Matters

✅ **Before (Problem):**
- Your IP: `192.168.1.4`
- Connect to mobile hotspot
- New IP: `10.220.117.150`
- ❌ Share links still use old IP
- ❌ Links don't work!

✅ **After (Solution):**
- App auto-detects current IP on startup
- Share links always work
- No manual configuration needed
- Works on WiFi, hotspot, ethernet

### Manual IP Override

If you want to force a specific IP:
```properties
app.base-url=http://YOUR_SPECIFIC_IP:8080
```

## 🔒 Security Best Practices

### 1. Never Commit Secrets

❌ **DON'T:**
```properties
# application.properties
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
encryption.master-key=cloudvault-secure-key-2024
```

✅ **DO:**
```properties
# application.properties.example
supabase.key=YOUR_SUPABASE_ANON_KEY
encryption.master-key=YOUR_ENCRYPTION_KEY_HERE
```

### 2. Use Environment Variables in Production

```bash
export SPRING_DATASOURCE_PASSWORD=your_password
export SUPABASE_KEY=your_key
export ENCRYPTION_MASTER_KEY=your_encryption_key
```

### 3. Generate Strong Encryption Keys

```bash
# Generate a random 32-character key
openssl rand -base64 32
```

## 📤 Pushing to GitHub

### First Time Setup

1. **Initialize Git (if not already done):**
   ```bash
   git init
   ```

2. **Check what will be committed:**
   ```bash
   git status
   ```
   
   Make sure `application.properties` is NOT listed!

3. **Add files:**
   ```bash
   git add .
   ```

4. **Commit:**
   ```bash
   git commit -m "Initial commit: CloudVault file storage system"
   ```

5. **Create GitHub repository:**
   - Go to github.com
   - Click "New repository"
   - Name it "cloudvault"
   - DON'T initialize with README (you already have one)

6. **Push to GitHub:**
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/cloudvault.git
   git branch -M main
   git push -u origin main
   ```

### Verify Security

After pushing, check GitHub to ensure:
- ❌ `application.properties` is NOT visible
- ✅ `application.properties.example` IS visible
- ✅ No database passwords visible
- ✅ No API keys visible

## 🔄 Updating Your Repository

```bash
# After making changes
git add .
git commit -m "Description of changes"
git push
```

## 🤝 Collaborating with Others

When someone clones your repo:

1. They get all the code
2. They DON'T get your credentials (protected by .gitignore)
3. They copy `application.properties.example` to `application.properties`
4. They add their own Supabase credentials
5. They can run the app immediately

## 📞 Need Help?

- Check if file is gitignored: `git check-ignore -v filename`
- See what's being tracked: `git ls-files`
- Accidentally committed secrets? [GitHub Secret Scanning](https://docs.github.com/en/code-security/secret-scanning)

---

**Remember:** Once you push code to GitHub, assume it's public forever. Never commit secrets!
