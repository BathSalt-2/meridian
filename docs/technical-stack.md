# MERIDIAN — Technical Stack and Device Requirements

---

## Full Technology Stack

### AI and Inference Layer

| Component | Technology | Configuration |
|-----------|------------|---------------|
| Primary on-device model | Gemma 4 E4B | Unsloth Dynamic GGUF 2.0, Q4 primary / Q8 for critical layers |
| Regional hub model | Gemma 4 26B MoE | Q4 quantization on Raspberry Pi 5 |
| Fine-tuning framework | Unsloth | Agricultural + ecological domain adaptation |
| On-device runtime | LiteRT (Google AI Edge) | GPU/NPU acceleration via Qualcomm Hexagon, MediaTek APU, Mali |
| Fallback runtime (CPU) | llama.cpp | E2B Q4 fallback for sub-4GB RAM devices |

### Application Layer

| Component | Technology | Notes |
|-----------|------------|-------|
| Primary platform | Kotlin / Compose Multiplatform | Android primary, Raspberry Pi hub via JVM |
| UI framework | Jetpack Compose | Material 3 design system |
| Navigation | Compose Navigation | Single-activity architecture |
| Async / concurrency | Kotlin Coroutines + Flow | State streams for LOCUS updates |
| Dependency injection | Hilt | Module-scoped component graphs |

### Data Layer

| Component | Technology | Purpose |
|-----------|------------|--------|
| Knowledge graph | SQLite (via Room) | LOCUS node and edge storage |
| Media cache | Hive | Binary observation thumbnails |
| Preferences | DataStore (Proto) | Configuration, community profile |
| Knowledge library | SQLite FTS5 | Full-text searchable offline corpus |
| Encryption | AES-256 (via AndroidKeyStore) | All LOCUS data encrypted at rest |

### Networking and Mesh Layer

| Component | Technology | Purpose |
|-----------|------------|--------|
| Community mesh sync | Wi-Fi Direct + Bluetooth LE | Device-to-device LOCUS synchronization |
| Hub sync | Wi-Fi (local) + PSALM libp2p | Hub-to-device sync at connectivity points |
| PSALM integration | libp2p via Cactus P2P Framework | Inter-community mesh data sharing |
| ZK proof integration | PSALM Lite-ZK | Data provenance certification |

### Multimodal Processing

| Component | Technology | Notes |
|-----------|------------|-------|
| Vision input | Gemma 4 E4B native vision encoder | Variable resolution, guided framing |
| Audio input | Gemma 4 E4B native audio encoder (USM conformer) | Up to 30-second audio samples |
| Voice interface | Gemma 4 E4B speech processing | 140+ languages, code-switching |
| TTS output | Android TTS + custom agricultural vocabulary | Synthesized voice responses |
| Camera | CameraX API | Guided framing overlays |

---

## Device Requirements and Performance

### Android Device Profiles

| Profile | RAM | Storage | Model | Inference Speed | Battery / Session | Notes |
|---------|-----|---------|-------|----------------|-------------------|-------|
| Entry (minimum viable) | 3–4 GB | 32 GB | E2B Q4 (LiteRT) | 6–9 tok/sec | ~8% per 15 min | Reduced PRAXIS reasoning depth |
| Standard (primary target) | 6–8 GB | 64 GB | E4B Q4 (LiteRT) | 12–18 tok/sec | ~12% per 15 min | Full PRAXIS enabled |
| Flagship | 12 GB+ | 128 GB | E4B Q8 (LiteRT) | 20–30 tok/sec | ~15% per 15 min | Higher inference quality |

### Regional Hub — Raspberry Pi 5

| Spec | Value |
|------|-------|
| RAM | 8 GB |
| Storage | 64 GB SD card (Class 10 minimum) |
| Model | Gemma 4 26B MoE Q4 |
| Inference speed | 8–12 tok/sec |
| Power | Mains (5V/5A USB-C) |
| Connectivity | Wi-Fi 5 / Ethernet |
| OS | Raspberry Pi OS Lite (64-bit) |

### Context Window Usage

| Use Case | Typical Context Length | Notes |
|----------|----------------------|-------|
| IRIS single observation | 4K–8K tokens | Image + observation history for place |
| PRAXIS single alert | 8K–16K tokens | Relevant LOCUS history + knowledge library excerpts |
| Seasonal forecast | 16K–48K tokens | Multi-month observation log |
| Strategic analysis (hub) | 64K–128K tokens | Full seasonal record + regional data |

**Important:** The 128K context window is technically supported but thermal throttling occurs on mobile devices above ~32K active context. For real-time interactive use, context is bounded at 16K. Longer context operations are processed as background async tasks.

---

## Security Architecture

| Layer | Mechanism |
|-------|----------|
| Data at rest | AES-256 via AndroidKeyStore; key derived from community PIN via PBKDF2 |
| Device sync | End-to-end encrypted over local mesh; no external transit |
| PSALM export | Anonymized + ZK-proof certified; raw data never transmitted |
| Network requests (hub sync) | TLS 1.3 with certificate pinning |
| Authentication | Community PIN + optional biometric (device-level) |
| Permission model | Camera, microphone, and Wi-Fi Direct requested at first use with clear purpose explanation |

---

## Build and CI/CD

| Component | Technology |
|-----------|------------|
| Build system | Gradle (KTS) |
| CI/CD | GitHub Actions |
| Testing — unit | JUnit 5 + MockK |
| Testing — integration | Espresso + Compose Test |
| Testing — IRIS | Custom evaluation harness against annotated agricultural image test set |
| Code quality | Detekt + ktlint |
| Artifact distribution | Internal: GitHub Releases / External: Google Play (post-hackathon) |
