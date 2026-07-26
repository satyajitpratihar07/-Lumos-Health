# 🩺 Lumos Health — Non-Invasive Optical Telemetry & Dermal AI Platform

![Android](https://img.shields.io/badge/Android-14%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![MongoDB Atlas](https://img.shields.io/badge/MongoDB-Atlas%20Cloud-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Groq AI](https://img.shields.io/badge/AI-Groq%20LLaMA--3.3--70B-FF6F00?style=for-the-badge&logo=openai&logoColor=white)

> **Lumos Health** transforms any standard smartphone camera into a non-invasive vital signs monitor and dermal AI diagnostic terminal. Powered by **Optical Remote Photoplethysmography (rPPG)**, computer vision, and real-time LLM medical reasoning.

---

## 📌 GitHub Repository Quick Info

### Short Description (For GitHub Repository Header):
```text
Lumos Health is an AI-powered non-invasive optical telemetry app that turns any smartphone camera into a vital signs & dermal health diagnostic tool using rPPG, Jetpack Compose, Groq LLaMA-3.3-70B, and MongoDB Atlas.
```

### Suggested Repository Topics / Tags:
`android` • `kotlin` • `jetpack-compose` • `rppg` • `computer-vision` • `ai-healthcare` • `llama3` • `groq` • `mongodb-atlas` • `firebase-auth` • `vital-signs` • `healthtech`

---

## 📱 Application Screenshots

<p align="center">
  <img src="docs/screenshots/login.png" width="24%" alt="Login Screen" />
  <img src="docs/screenshots/dashboard.png" width="24%" alt="Dashboard Screen" />
  <img src="docs/screenshots/facescan.png" width="24%" alt="Live Face Scan" />
  <img src="docs/screenshots/analysis.png" width="24%" alt="Realtime Analysis" />
</p>

<p align="center">
  <img src="docs/screenshots/report.png" width="24%" alt="Biomarker Report" />
  <img src="docs/screenshots/aichatbot.png" width="24%" alt="AI Clinical Chatbot" />
  <img src="docs/screenshots/history.png" width="24%" alt="Scan History" />
  <img src="docs/screenshots/setting.png" width="24%" alt="Settings & Profile" />
</p>

---

## 🌟 Key Features

- **🫀 Optical rPPG Telemetry**: Real-time measurement of Heart Rate (BPM), Heart Rate Variability (HRV), Blood Pressure estimates, Respiratory Rate, and SpO₂ without external wearables.
- **🔬 Facial Behavior & Dermal AI**: Calculates a personalized **Vitality Index Score (0–100%)** alongside dermal metrics (hydration, acne risk, dark circles, wrinkles, and eye blink rate).
- **🤖 Groq LLaMA-3.3-70B Health Assistant**: Conversational AI assistant augmented with live search context (openFDA, Wikipedia, and DuckDuckGo APIs).
- **🎨 Glassmorphic Telemetry UI**: Translucent dark mode design featuring smooth horizontal scrolling medical ECG waveforms, laser sweep beams, and pulsing telemetry nodes.
- **🔑 Google Authentication**: One-Tap Google Sign-In with official 4-color Google "G" branding and persistent local session lock.
- **🛡️ Enterprise Security & MongoDB Atlas Sync**: Dual-database architecture using **Room SQLite** for zero-latency offline storage and **MongoDB Atlas Cloud** for encrypted HTTPS REST sync with SHA-256 digital tamper signatures.

---

## 🛠️ Architecture & Tech Stack

```
 ┌─────────────────────────────────────────────────────────────┐
 │                      Lumos Health App                       │
 └──────────────┬──────────────────────────────┬───────────────┘
                │                              │
     Offline-First Local Sync        Encrypted HTTPS REST Sync
                ▼                              ▼
 ┌─────────────────────────────┐┌──────────────────────────────┐
 │     Room SQLite Database    ││      MongoDB Atlas Cloud     │
 │  - Local Offline Backup     ││  - patient_reports Collection│
 │  - Instant Zero-Latency     ││  - users Collection          │
 └─────────────────────────────┘│  - user_logins Collection    │
                                └──────────────────────────────┘
```

| Component | Technology |
|---|---|
| **Language & SDK** | Kotlin, Android SDK 34/36, Gradle (KTS) |
| **UI Framework** | Jetpack Compose, Material Design 3, Glassmorphism Canvas |
| **Camera & Image Processing** | Android CameraX API, Hardware Image Analysis |
| **Local Database** | Room Database (SQLite) with Kotlin Coroutines & Flow |
| **Cloud Database** | MongoDB Atlas (`mongodb-driver-sync` + OkHttp HTTPS REST API) |
| **Authentication** | Firebase Auth + Google Credential Manager API |
| **LLM & Search Engines** | Groq LLaMA-3.3-70B Versatile, openFDA, Wikipedia API |

---

## 🚀 Getting Started

### 1. Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer.
- Android device or emulator running **Android 14+ (API Level 34+)** with a camera.
- Java JDK 17+.

### 2. Clone & Build
```bash
git clone https://github.com/your-username/lumos-health.git
cd lumos-health
```

### 3. Open in Android Studio
1. Open Android Studio → **File → Open** → Select `lumos-health`.
2. Sync project with Gradle files.
3. Click **Run ▶** (`Shift + F10`) to deploy to your device!

---

## 🔒 Security & Data Encryption

Lumos Health enforces end-to-end data security:
- **SHA-256 Password Protection**: Passwords are hashed locally before storage.
- **Cryptographic Checksums (`securityChecksum`)**: Digital SHA-256 signatures are generated for every medical report payload (`userEmail + timestamp + heartRate + wellnessScore`) to prevent data tampering.
- **TLS 1.3 Transport**: Encrypted HTTPS REST communication ensures safe data transfer across public Wi-Fi and mobile networks.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
