# CloudVault Startup Script
# Double-click this file to start CloudVault

Write-Host "🚀 Starting CloudVault..." -ForegroundColor Cyan

# Set Java and Maven paths
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path += ";C:\Users\Dell\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin"

# Navigate to project directory
Set-Location "D:\ccmicroprojectfinal"

Write-Host "☁️  CloudVault is starting..." -ForegroundColor Green
Write-Host "📍 Local access: http://localhost:8080" -ForegroundColor Yellow
Write-Host "📱 Network access: http://$(Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -notlike '127.*' -and $_.IPAddress -notlike '169.*'} | Select-Object -First 1 -ExpandProperty IPAddress):8080" -ForegroundColor Yellow
Write-Host ""
Write-Host "⏹️  Press Ctrl+C to stop the server" -ForegroundColor Red
Write-Host ""

# Start Spring Boot
mvn spring-boot:run
