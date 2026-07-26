<h1 align="center">
  
  🩺 Lumos Health
  <br>
</h1>

<h3 align="center">AI-Powered Non-Invasive Optical Telemetry & Dermal Diagnostic Platform</h3>

<p align="center">
  <a href="https://github.com/satyajitpratihar07/-Lumos-Health">
    <img src="https://img.shields.io/badge/Android-14%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 14+">
    <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin 2.0">
    <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
    <img src="https://img.shields.io/badge/MongoDB-Atlas%20Cloud-47A248?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB Atlas">
    <img src="https://img.shields.io/badge/AI-Groq%20LLaMA--3.3--70B-FF6F00?style=for-the-badge&logo=openai&logoColor=white" alt="Groq LLaMA 3.3 70B">
    <img src="https://img.shields.io/badge/Security-SHA--256%20Encrypted-D9381E?style=for-the-badge&logo=shield&logoColor=white" alt="SHA-256 Security">
  </a>
</p>

<p align="center">
  <img src="https://user-images.githubusercontent.com/73097560/115834477-dbab4500-a447-11eb-908a-139a6edaec5c.gif" width="100%" alt="Heartbeat Waveform Divider">
</p>

> **Lumos Health** transforms any standard smartphone camera into a non-invasive vital signs monitor and dermal AI diagnostic terminal. Powered by **Optical Remote Photoplethysmography (rPPG)**, computer vision, and real-time LLM medical reasoning.

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

## ⚡ Real-Time Optical Telemetry Capabilities

| Biomarker Metric | Detection Methodology | Output Range | Clinical Significance |
|---|---|---|---|
| **Heart Rate (BPM)** | Sub-dermal green/red spectrum pulse absorption | 45 – 180 BPM | Primary cardiac pulse rate indicator |
| **Heart Rate Variability (HRV)** | Inter-beat interval (IBI) peak-to-peak analysis | 20 – 120 ms | Autonomic nervous system & stress index |
| **Blood Oxygen (SpO₂)** | Multi-spectrum oxygenated hemoglobin light ratio | 90% – 100% | Respiratory system efficiency estimate |
| **Blood Pressure** | Pulse wave transit velocity & arterial stiffness inference | 90/60 – 160/100 mmHg | Systemic vascular resistance estimation |
| **Vitality Index** | Multi-biomarker weighted fusion matrix | 0% – 100% | Comprehensive holistic health score |
| **Dermal Hydration** | Facial specular reflection & texture frequency | 0% – 100% | Skin barrier hydration and moisture retention |

---

## 🔬 Optical rPPG & AI Processing Pipeline

```
  ┌──────────────────────┐      ┌──────────────────────┐      ┌──────────────────────┐
  │  CameraX RGB Frame   │ ───► │ Sub-Dermal Chromatic │ ───► │ Fast Fourier Transform│
  │  Extraction (30 FPS) │      │ Pulse Decomposition  │      │ (FFT Peak Frequency) │
  └──────────────────────┘      └──────────────────────┘      └──────────┬───────────┘
                                                                         │
  ┌──────────────────────┐      ┌──────────────────────┐                 │
  │ MongoDB Atlas Cloud  │ ◄─── │ Groq LLaMA-3.3-70B   │ ◄───────────────┘
  │ SHA-256 HTTPS Sync   │      │ Medical AI Engine    │
  └──────────────────────┘      └──────────────────────┘
```

---

## 🌟 Key Features

- **🫀 Optical rPPG Telemetry**: Real-time measurement of Heart Rate (BPM), Heart Rate Variability (HRV), Blood Pressure estimates, Respiratory Rate, and SpO₂ without external wearables.
- **🔬 Facial Behavior & Dermal AI**: Calculates a personalized **Vitality Index Score (0–100%)** alongside dermal metrics (hydration, acne risk, dark circles, wrinkles, and eye blink rate).
- **🤖 Groq LLaMA-3.3-70B Health Assistant**: Conversational AI assistant augmented with live search context (openFDA, Wikipedia, and DuckDuckGo APIs).
- **🎨 Glassmorphic Telemetry UI**: Translucent dark mode design featuring smooth horizontal scrolling medical ECG waveforms, laser sweep beams, and pulsing telemetry nodes.
- **🔑 Google Authentication**: One-Tap Google Sign-In with official 4-color Google "G" branding and persistent local session lock.
- **🛡️ Enterprise Security & MongoDB Atlas Sync**: Dual-database architecture using **Room SQLite** for zero-latency offline storage and **MongoDB Atlas Cloud** for encrypted HTTPS REST sync with SHA-256 digital tamper signatures.

---

## 🛠️ Technology Stack

| Layer | Technologies Used |
|---|---|
| **Mobile OS & Language** | Android 14+ (API 34/36), Kotlin 2.0 |
| **UI Framework** | Jetpack Compose, Material Design 3, Glassmorphism Canvas |
| **Camera & Vision** | CameraX API, Hardware Image Analysis |
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
git clone https://github.com/satyajitpratihar07/-Lumos-Health.git
cd -Lumos-Health
```

### 3. Open in Android Studio
1. Open Android Studio → **File → Open** → Select `-Lumos-Health`.
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
