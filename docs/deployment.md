# MERIDIAN — Deployment Guide

---

## Overview

MERIDIAN is designed for deployment in resource-constrained, low-connectivity environments. The deployment model is deliberately simple: no server infrastructure is required for core functionality. The Android application is fully self-contained.

This guide covers three deployment scenarios:
1. Single-device community deployment
2. Multi-device community mesh deployment
3. Regional hub deployment (Raspberry Pi 5 + Gemma 4 26B MoE)

---

## Prerequisites

### Android Device Requirements
- Android 9.0 (API 28) minimum
- 3 GB RAM minimum (6–8 GB recommended)
- 4 GB free storage minimum (8 GB recommended for full knowledge library)
- Camera (minimum 8MP recommended for reliable IRIS crop analysis)
- Microphone (built-in adequate for acoustic monitoring)
- Bluetooth 4.2+ and Wi-Fi 5 (for mesh sync)

### Community Requirements
- At least one community member willing to lead the onboarding process
- Community agreement on a shared PIN for LOCUS encryption
- Two to three hours for the initial onboarding sessions
- Elder participation for traditional knowledge capture session

---

## Android Deployment — Single Device

### Installation

```bash
# Sideload via ADB (development / field deployment)
adb install meridian-release.apk

# Or distribute via Google Play (post-hackathon public release)
# Package name: ai.or4cl3.meridian
```

### First Launch
1. Set community name and PIN
2. Select primary language(s) for the community
3. Download and verify knowledge library (Wi-Fi required for first download; cached locally thereafter)
4. Begin onboarding Session 1 (community mapping)

### Model Loading
On first launch, MERIDIAN downloads and installs the Gemma 4 E4B GGUF model bundle:
- E4B Q4 primary model: ~5.2 GB
- Agricultural domain adapter: ~180 MB
- Total first-install download: ~5.4 GB

After installation, the model is cached locally. No subsequent downloads are required for inference. Knowledge library updates can be pulled during hub sync events without re-downloading the full model.

---

## Android Deployment — Multi-Device Community Mesh

### Setup
1. Install MERIDIAN on all community devices
2. On each device, enter the same community name and PIN during setup
3. Enable mesh sync in Settings → Community Mesh → Enable
4. Devices will automatically discover and sync with each other when in proximity

### Sync Architecture
- Primary sync: Wi-Fi Direct (range ~200m, throughput adequate for full LOCUS sync)
- Secondary sync: Bluetooth LE (range ~50m, for lightweight observation-only sync)
- Sync is automatic when devices detect each other
- No manual action required beyond proximity

### Conflict Resolution
If the same Place node or Observation is modified on two devices between syncs, MERIDIAN resolves conflicts using:
1. Timestamp (most recent modification wins)
2. IRIS confidence score (higher confidence observation supersedes lower confidence)
3. Manual review flag for high-stakes conflicts (e.g., contradictory disease diagnoses at the same place within 48 hours)

### Recommended Minimum Community Deployment
- 3–5 devices for a community of 20–50 farming households
- Distribute devices among households in different geographic areas of community land
- Designate one device as the "primary" for hub sync events (market visits)

---

## Regional Hub Deployment — Raspberry Pi 5

### Hardware Setup

```
Required:
  Raspberry Pi 5 (8GB RAM)
  64GB+ SD card (Class 10 / A2 rating recommended)
  Official Pi 5 power supply (5V/5A)
  Weatherproof enclosure (for field deployment)
  Wi-Fi access point or hotspot capability

Optional:
  Ethernet connection (for connectivity-point deployments)
  UPS battery backup (for reliability in unreliable power environments)
  External SSD (for improved I/O performance with large LOCUS aggregations)
```

### Software Installation

```bash
# Flash Raspberry Pi OS Lite (64-bit) to SD card
# Boot and configure

# Install MERIDIAN hub software
curl -fsSL https://meridian.or4cl3.ai/install-hub.sh | bash

# This script:
# 1. Installs llama.cpp with ARM NEON optimizations
# 2. Downloads Gemma 4 26B MoE Q4 GGUF (~18GB)
# 3. Installs the MERIDIAN hub service (systemd)
# 4. Configures PSALM libp2p mesh node
# 5. Sets up the Wi-Fi access point for community device sync

# Start the hub service
sudo systemctl start meridian-hub
sudo systemctl enable meridian-hub
```

### Hub Operation
- Hub broadcasts a local Wi-Fi network (SSID: `MERIDIAN-HUB-[community-id]`)
- Community devices connect automatically when in range
- Sync protocol: device connects → anonymized LOCUS delta uploads → knowledge updates download → disconnect
- Typical sync time: 45–90 seconds per device
- Hub log available at `/var/log/meridian-hub/sync.log`

### Hub Connectivity
When external connectivity is available at the hub location (mobile data, satellite, occasional ethernet):
- Hub downloads updated knowledge library packages
- Hub downloads IRIS calibration updates (new disease variant models)
- Hub pushes anonymized regional aggregates to PSALM network
- Hub receives regional threat intelligence from PSALM mesh

All external connectivity is opportunistic — the hub operates fully independently when offline.

---

## Offline Knowledge Library Management

### Initial Download
The full knowledge library (~800MB) downloads once during app setup. Requires Wi-Fi.

### Updates
Knowledge library updates are distributed via hub sync. No internet connection required on community devices after the initial download.

### Regional Language Packs
Additional language-specific agricultural content packs are available for download. Priority packs currently available:
- East Africa (Swahili, Amharic, Somali, Tigrinya)
- West Africa (Hausa, Yoruba, Igbo, Twi, Wolof)
- South Asia (Hindi, Bengali, Tamil, Telugu, Urdu)
- Southeast Asia (Bahasa, Tagalog, Khmer, Burmese)
- Latin America (Spanish regional variants, Quechua, Guaraní)

---

## Field Support and Troubleshooting

### Common Issues

**IRIS crop analysis slow or failing**
- Check device temperature (overheating throttles inference — move to shade)
- Ensure adequate storage space (model temp files require ~2GB free)
- Verify LiteRT GPU delegation is active in Settings → Performance

**Mesh sync not connecting**
- Ensure both devices have mesh sync enabled
- Move devices within 30m of each other for initial Bluetooth LE discovery
- Manually initiate sync: Settings → Community Mesh → Sync Now

**LOCUS data appears incomplete on a device**
- Perform a forced full sync: Settings → Community Mesh → Full Sync
- This syncs the complete LOCUS model rather than just the delta
- Requires ~10 minutes and Wi-Fi Direct connection

**Hub not recognized by community devices**
- Verify hub Wi-Fi broadcast is active: `sudo systemctl status meridian-hub`
- Check community ID matches between hub config and device settings
- Re-run hub setup script: `meridian-hub --reconfigure`

### Support Resources
- GitHub Issues: [github.com/BathSalt-2/meridian/issues](https://github.com/BathSalt-2/meridian/issues)
- Community forum (post-hackathon): meridian.or4cl3.ai/community
