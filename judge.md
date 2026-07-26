# 🩺 Lumos Health — Facial Behavior & Dermal AI Biomarker Platform
> **AI-Powered Non-Invasive Optical Telemetry & Clinical Diagnostic Companion**

---

## 🌟 Project Overview

**Lumos Health** is an advanced mobile health platform that turns any standard smartphone camera into a non-invasive vital signs monitor and dermal AI diagnostic tool. By leveraging **optical Remote Photoplethysmography (rPPG)**, computer vision, and Large Language Model (LLM) medical reasoning, Lumos Health provides real-time physiological insights without requiring external wearables or specialized hardware.

---

## 🚀 Key Features & Capabilities

### 1. 🫀 Optical rPPG & Cardiac Telemetry
- **Remote Heart Rate (BPM)**: Detects subtle sub-dermal color changes caused by cardiac blood pulse cycles.
- **Heart Rate Variability (HRV)**: Measures inter-beat intervals to evaluate autonomic nervous system balance.
- **Blood Oxygen Saturation (SpO₂ Estimate)**: Analyzes multi-spectrum light absorption to approximate blood oxygen levels.
- **Blood Pressure & Respiratory Rate Telemetry**: Infers systemic vascular resistance and breathing rate from pulse waveforms.

### 2. 🔬 Facial Behavior & Dermal AI Diagnostics
- **Vitality Index**: Calculates a holistic **Overall Wellness Score (0–100%)** categorized into *Optimal*, *Normal*, or *Attention Required*.
- **Dermal Analysis**: Assesses skin hydration, oiliness, acne risk, wrinkles, dark circles, and pigmentation.
- **Neurological & Ocular Indicators**: Tracks eye blink rate, drowsiness index, stress score, fatigue level, and anxiety indicators.

### 3. 💬 Groq LLaMA-3.3-70B Real-Time Clinical Assistant
- Integrated conversational AI health assistant powered by **Groq LLaMA-3.3 70B Versatile**.
- Augmented with **Live Medical Context Integration** (DuckDuckGo Instant Answers + Wikipedia API + openFDA Drug Safety API) for up-to-date medical references.
- Provides actionable lifestyle guidance and triage advice based on scanned biomarker metrics.

### 4. 🎨 Modern Glassmorphic Telemetry UI
- Built with **100% Jetpack Compose** using custom glassmorphic translucent UI design system.
- **Animated Medical ECG/PPG Waveform**: Features smooth horizontal scrolling pulse waves, glowing area fills, dashed gridlines, and sweeping radar laser scan dots.
- **Profile Header**: Displays user DP image / initial avatar on the **left** with personalized greetings on the **right**.

### 5. 🔑 Google Authentication & Persistent Sessions
- **One-Tap Google Sign-In**: Seamless authentication featuring official 4-color Google "G" branding.
- **Persistent Local Session Lock**: Users remain logged in across app closes, backgrounding, and phone reboots until explicitly tapping **Secure Log Out** in Settings.

---

## 🛡️ Architecture, Security & Database Sync

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

### 1. Dual Database Architecture
- **Room SQLite (Offline-First)**: Ensures instant UI responsiveness and offline functionality even without internet access.
- **MongoDB Atlas Cloud**: Automatically streams all user registrations, timestamped login events, and patient biomarker reports to a centralized cloud cluster (`FaceHealthMonitorDB`).

### 2. Cryptographic Security & Data Protection
- **SHA-256 Password Protection**: Hashes user credentials locally before persistence.
- **Cryptographic Checksums (`securityChecksum`)**: Generates SHA-256 tamper-evident digital signatures for every medical report payload (`userEmail + timestamp + heartRate + wellnessScore`).
- **TLS 1.3 Encrypted Transit**: Uses HTTPS REST payloads (Port 443) to bypass Android raw TCP socket limitations and ensure safe data delivery across mobile networks.

---

## 🛠️ Technology Stack

| Layer | Technologies Used |
|---|---|
| **Mobile OS & Language** | Android 14+ (API 34/36), Kotlin 2.x |
| **UI Framework** | Jetpack Compose, Material Design 3, Glassmorphism CSS/Canvas |
| **Camera & Vision** | CameraX API, Android Hardware Processing |
| **Local Database** | Room Database (SQLite), SharedFlow / StateFlow |
| **Cloud Database** | MongoDB Atlas (`mongodb-driver-sync` + HTTPS REST API) |
| **Cloud Auth** | Firebase Authentication + Google Credential Manager API |
| **AI / LLM Engine** | Groq LLaMA-3.3-70B Versatile + DuckDuckGo + openFDA APIs |

---

## 📊 MongoDB Atlas Collection Structure

### 1. `users` Collection
```json
{
  "email": "user@example.com",
  "name": "Alex Smith",
  "gender": "Male",
  "age": 28,
  "heightCm": 178.0,
  "weightKg": 72.0,
  "avatarSeed": "https://lh3.googleusercontent.com/...",
  "registeredAt": 1785040000000,
  "securityHash": "a8f5f167f44f4964e6c998dee827110c...",
  "isEncrypted": true
}
```

### 2. `patient_reports` Collection
```json
{
  "userEmail": "user@example.com",
  "timestamp": "2026-07-26 11:30",
  "heartRate": 72,
  "spo2": 98,
  "bloodPressure": "120/80 mmHg",
  "hrv": 55,
  "overallWellnessScore": 88,
  "wellnessCategory": "Optimal",
  "skinAcne": "Low",
  "skinHydration": 85,
  "securityChecksum": "c3ab8ff13720e8ad9047dd39466b3c89...",
  "isEncrypted": true,
  "encryptionAlgorithm": "SHA-256 + TLS 1.3"
}
```

---

## 🏆 Summary for Hackathon Judges

Lumos Health bridges the gap between **computer vision telemetry**, **AI clinical insights**, and **enterprise-grade data security**. It turns everyday smartphones into accessible health diagnostic tools while enforcing strict end-to-end data encryption and real-time MongoDB cloud synchronization.
