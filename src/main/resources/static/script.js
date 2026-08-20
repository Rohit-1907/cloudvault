// CloudVault Frontend JavaScript

const API_BASE = '/api/files';

// DOM Elements
const uploadArea = document.getElementById('uploadArea');
const fileInput = document.getElementById('fileInput');
const uploadForm = document.getElementById('uploadForm');
const expirySelect = document.getElementById('expirySelect');
const accessModeSelect = document.getElementById('accessModeSelect');
const uploadBtn = document.getElementById('uploadBtn');
const uploadStatus = document.getElementById('uploadStatus');
const uploadSection = document.getElementById('uploadSection');
const resultSection = document.getElementById('resultSection');
const downloadForm = document.getElementById('downloadForm');
const shareTokenInput = document.getElementById('shareTokenInput');
const downloadStatus = document.getElementById('downloadStatus');
const fileInfoResult = document.getElementById('fileInfoResult');

let selectedFile = null;

// ============ Upload Area Interactions ============

// Click to select file
uploadArea.addEventListener('click', () => fileInput.click());

// File input change
fileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        selectedFile = file;
        uploadArea.style.borderColor = '#059669';
        uploadArea.innerHTML = `
            <p style="color: #059669;">✅ File selected: <strong>${file.name}</strong></p>
            <p style="color: #64748b; font-size: 0.9em;">Size: ${formatFileSize(file.size)}</p>
        `;
    }
});

// Drag and drop
uploadArea.addEventListener('dragover', (e) => {
    e.preventDefault();
    uploadArea.classList.add('drag-over');
});

uploadArea.addEventListener('dragleave', () => {
    uploadArea.classList.remove('drag-over');
});

uploadArea.addEventListener('drop', (e) => {
    e.preventDefault();
    uploadArea.classList.remove('drag-over');
    
    const files = e.dataTransfer.files;
    if (files.length > 0) {
        selectedFile = files[0];
        fileInput.files = files;
        uploadArea.style.borderColor = '#059669';
        uploadArea.innerHTML = `
            <p style="color: #059669;">✅ File selected: <strong>${selectedFile.name}</strong></p>
            <p style="color: #64748b; font-size: 0.9em;">Size: ${formatFileSize(selectedFile.size)}</p>
        `;
    }
});

// ============ Upload Form Submission ============

uploadForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!selectedFile) {
        showStatus(uploadStatus, 'Please select a file first', 'error');
        return;
    }

    if (!expirySelect.value) {
        showStatus(uploadStatus, 'Please select an expiry duration', 'error');
        return;
    }

    if (!accessModeSelect.value) {
        showStatus(uploadStatus, 'Please select an access mode', 'error');
        return;
    }

    await uploadFile();
});

async function uploadFile() {
    try {
        showStatus(uploadStatus, 'Uploading and encrypting file...', 'loading');
        uploadBtn.disabled = true;

        const formData = new FormData();
        formData.append('file', selectedFile);
        formData.append('expiry', expirySelect.value);
        formData.append('accessMode', accessModeSelect.value);

        const response = await fetch(`${API_BASE}/upload`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Upload failed');
        }

        const data = await response.json();
        showUploadResult(data);
        showStatus(uploadStatus, '✅ File uploaded successfully!', 'success');

    } catch (error) {
        console.error('Upload error:', error);
        showStatus(uploadStatus, `❌ Upload failed: ${error.message}`, 'error');
        uploadBtn.disabled = false;
    }
}

function showUploadResult(data) {
    uploadSection.style.display = 'none';
    resultSection.style.display = 'block';

    document.getElementById('resultFileName').textContent = data.fileName;
    document.getElementById('resultFileSize').textContent = formatFileSize(data.fileSize);
    document.getElementById('resultUploadTime').textContent = formatDateTime(data.uploadedAt);
    document.getElementById('resultExpiryTime').textContent = formatDateTime(data.expiresAt);
    document.getElementById('shareLink').value = data.shareLink;
    document.getElementById('shareToken').textContent = data.shareToken;
    
    // Display access mode
    const accessModeText = data.accessMode === 'DOWNLOAD' 
        ? '📥 Download & View (Recipients can download)' 
        : '👁️ View Only (Recipients cannot download)';
    document.getElementById('accessMode').textContent = accessModeText;

    // Scroll to result
    resultSection.scrollIntoView({ behavior: 'smooth' });
}

// ============ Download Section ============

downloadForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const token = shareTokenInput.value.trim().toUpperCase();

    if (!token || token.length !== 16) {
        showStatus(downloadStatus, 'Please enter a valid 16-character share token', 'error');
        return;
    }

    await checkFileAndShowInfo(token);
});

async function checkFileAndShowInfo(token) {
    try {
        showStatus(downloadStatus, 'Checking file availability...', 'loading');

        const response = await fetch(`${API_BASE}/access/${token}`);

        if (!response.ok) {
            if (response.status === 404) {
                showStatus(downloadStatus, '❌ File not found or expired', 'error');
            } else {
                showStatus(downloadStatus, '❌ Error checking file', 'error');
            }
            fileInfoResult.style.display = 'none';
            return;
        }

        const data = await response.json();

        if (data.isExpired) {
            showStatus(downloadStatus, '⏰ This file has expired and is no longer available', 'error');
            fileInfoResult.style.display = 'none';
            return;
        }

        showFileInfo(data, token);
        showStatus(downloadStatus, '✅ File is available for download', 'success');

    } catch (error) {
        console.error('Check error:', error);
        showStatus(downloadStatus, `❌ Error: ${error.message}`, 'error');
        fileInfoResult.style.display = 'none';
    }
}

function showFileInfo(data, token) {
    fileInfoResult.innerHTML = `
        <div class="file-info-item">
            <span class="file-info-label">📁 File Name:</span>
            <span class="file-info-value">${data.fileName}</span>
        </div>
        <div class="file-info-item">
            <span class="file-info-label">💾 File Size:</span>
            <span class="file-info-value">${formatFileSize(data.fileSize)}</span>
        </div>
        <div class="file-info-item">
            <span class="file-info-label">📅 Uploaded:</span>
            <span class="file-info-value">${formatDateTime(data.uploadedAt)}</span>
        </div>
        <div class="file-info-item">
            <span class="file-info-label">⏰ Expires:</span>
            <span class="file-info-value">${formatDateTime(data.expiresAt)}</span>
        </div>
        <div class="file-info-item">
            <span class="file-info-label">📥 Downloads:</span>
            <span class="file-info-value">${data.downloadCount}</span>
        </div>
        <div class="download-button-container">
            <button class="btn btn-primary" onclick="downloadFile('${token}')">
                ⬇️ Download File
            </button>
        </div>
    `;
    fileInfoResult.style.display = 'block';
}

async function downloadFile(token) {
    try {
        showStatus(downloadStatus, 'Downloading and decrypting file...', 'loading');

        const response = await fetch(`${API_BASE}/download/${token}`);

        if (!response.ok) {
            throw new Error('Download failed');
        }

        // Get filename from Content-Disposition header
        const contentDisposition = response.headers.get('content-disposition');
        let filename = 'file';
        if (contentDisposition) {
            const filenameMatch = contentDisposition.match(/filename="(.+?)"/);
            if (filenameMatch) {
                filename = filenameMatch[1];
            }
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        showStatus(downloadStatus, '✅ File downloaded successfully!', 'success');

    } catch (error) {
        console.error('Download error:', error);
        showStatus(downloadStatus, `❌ Download failed: ${error.message}`, 'error');
    }
}

// ============ Utility Functions ============

function showStatus(element, message, type) {
    element.innerHTML = message; // Changed to innerHTML to support icons
    element.className = `status-message ${type}`;
    element.style.display = 'flex';

    if (type === 'success') {
        setTimeout(() => {
            element.style.display = 'none';
        }, 5000);
    }
}

function startTransfer() {
    const uploadSection = document.getElementById('uploadSection');
    if (uploadSection) {
        uploadSection.scrollIntoView({ behavior: 'smooth' });
    }
}

function copyShareLink() {
    const shareLink = document.getElementById('shareLink');
    shareLink.select();
    document.execCommand('copy');

    showStatus(uploadStatus, '📋 Share link copied to clipboard!', 'success');
}

function resetForm() {
    selectedFile = null;
    fileInput.value = '';
    expirySelect.value = '';
    uploadStatus.style.display = 'none';
    resultSection.style.display = 'none';
    uploadSection.style.display = 'block';
    uploadBtn.disabled = false;

    uploadArea.style.borderColor = '#2563eb';
    uploadArea.innerHTML = `
        <svg class="upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM1 12c0 .55.45 1 1 1h6v4H2c-1.1 0-2-.9-2-2v-3zm14-6h6c1.1 0 2 .9 2 2v3h-8V6zm3.9 13.1l1.4-1.4c.39-.39.39-1.02 0-1.41-.39-.39-1.02-.39-1.41 0l-1.4 1.4c-.39.39-.39 1.02 0 1.41.39.39 1.02.39 1.41 0z"/>
            <path d="M15 6h-3V2H9v4H6l3-3 3 3z"/>
        </svg>
        <p class="upload-text">Drag and drop your file here or click to select</p>
    `;

    // Scroll to upload section
    uploadSection.scrollIntoView({ behavior: 'smooth' });
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// ============ Initialize ============

console.log('☁️ CloudVault Frontend Loaded');
