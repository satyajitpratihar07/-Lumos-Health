# 🚀 Step-by-Step GitHub Upload & Push Guide

This guide explains how to upload, update, and push your **Lumos Health** codebase to GitHub step-by-step.

---

## 📌 Repository Link
**URL**: `https://github.com/satyajitpratihar07/-Lumos-Health.git`

---

## 🛠️ Option 1: Quick 1-Command Automatic Push

If you ever make changes to your code in Android Studio or VS Code, open Terminal in your project root and run:

```bash
git add .
git commit -m "Update Lumos Health features & UI"
git push origin main
```

---

## 📜 Option 2: Step-by-Step Manual Upload Instructions

### Step 1: Open Terminal in Project Directory
Open PowerShell or Terminal inside your project directory:
```bash
cd "c:\Users\satya\Downloads\health app"
```

### Step 2: Initialize Git (If not already initialized)
```bash
git init
```

### Step 3: Connect to Remote GitHub Repository
```bash
git remote add origin https://github.com/satyajitpratihar07/-Lumos-Health.git
```
*(If origin already exists, set it with: `git remote set-url origin https://github.com/satyajitpratihar07/-Lumos-Health.git`)*

### Step 4: Stage All Files
```bash
git add .
```

### Step 5: Commit Your Changes
```bash
git commit -m "Initial commit: Lumos Health - Facial Behavior & Dermal AI Platform"
```

### Step 6: Set Main Branch & Push to GitHub
```bash
git branch -M main
git push -u origin main
```

---

## ⚠️ Troubleshooting Common Push Issues

### Issue 1: GitHub Secret Scanning Blocked Push
- **Cause**: GitHub blocks pushing files containing plain-text API keys.
- **Fix**: Move API keys to `.env` or `BuildConfig` before committing.

### Issue 2: Merge Conflict or Rejected Non-Fast-Forward Push
- **Fix**: Run force update if overwriting remote branch:
  ```bash
  git push -u origin main --force
  ```

---

## 🎉 Live Repository
Your code is live on GitHub at:
👉 **[https://github.com/satyajitpratihar07/-Lumos-Health](https://github.com/satyajitpratihar07/-Lumos-Health)**
