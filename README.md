<div align="center">

# MERIDIAN

### Multimodal Ecological Resilience Intelligence
### for Distributed Intelligence and Adaptive Networks

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Hackathon](https://img.shields.io/badge/Kaggle-Gemma_4_Good_Hackathon-orange)](https://www.kaggle.com/competitions/gemma-4-good-hackathon)
[![Track](https://img.shields.io/badge/Track-Climate_%26_Global_Resilience-green)](https://www.kaggle.com/competitions/gemma-4-good-hackathon)
[![Model](https://img.shields.io/badge/Powered_By-Gemma_4_E4B-purple)](https://deepmind.google/models/gemma/gemma-4/)
[![Org](https://img.shields.io/badge/By-Or4cl3_AI_Solutions-red)](https://github.com/BathSalt-2)
[![Deadline](https://img.shields.io/badge/Deadline-May_18_2026-yellow)](https://www.kaggle.com/competitions/gemma-4-good-hackathon)

> *"The farmers who need this most are not asking how to treat a single disease.*
> *They are asking what their grandfather knew — and the changing world took away."*

**MERIDIAN** is a living, recursive environmental intelligence system that builds a persistent
cognitive model of a community's ecological reality — synthesizing soil, crop, water, weather,
and health signals into proactive resilience intelligence.

**100% on-device. Zero connectivity required. Smarter with every interaction.**

[Architecture](docs/architecture.md) • [IRIS](docs/iris.md) • [LOCUS](docs/locus.md) • [PRAXIS](docs/praxis.md) • [Deployment](docs/deployment.md)

</div>

---

## The Problem

Approximately **500 million smallholder farmers** represent 70% of the world's food-insecure population. Climate change is dismantling their centuries-old pattern knowledge in a single generation. The institutions that generate agricultural intelligence — research stations, extension services, meteorological services — do not reach these communities.

What these communities *have* is generational ecological knowledge, calibrated to local conditions with extraordinary precision. What they are *losing* is the ability to apply that knowledge in a climate that no longer behaves the way it did when the knowledge was formed.

The rains come at the wrong time. Pests arrive in months when cold previously suppressed them. Soil that held moisture for decades no longer does. **The knowledge is intact. The world it was calibrated to has shifted.**

MERIDIAN is the bridge.

---

## What MERIDIAN Does

- **Sees** the physical world through multimodal AI — photographing crops, soil, water, and landscapes to detect threats before they become crises
- **Remembers** everything across seasons — building a living ecological memory that grows richer with every observation
- **Reasons** transparently — showing farmers not just what to do but exactly why, with full evidence chains
- **Acts proactively** — generating unprompted alerts based on patterns accumulating in the knowledge model
- **Speaks every language** — 140+ language support with voice-first interaction for low-literacy contexts
- **Connects communities** — sharing verified ecological intelligence across the PSALM mesh without transmitting sensitive data

---

## The Trinity Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         MERIDIAN                            │
│                                                             │
│  ┌───────────┐      ┌───────────┐      ┌────────────────┐  │
│  │   IRIS    │─────▶│   LOCUS   │─────▶│    PRAXIS      │  │
│  │           │      │           │      │                │  │
│  │  Sensory  │      │  Memory   │      │  Reasoning &   │  │
│  │   Layer   │      │   Layer   │      │ Alerting Layer │  │
│  └───────────┘      └───────────┘      └────────────────┘  │
│                                                             │
│  Multimodal          Knowledge           Proactive          │
│  Perception          Graph &             Intelligence       │
│  (Gemma 4 E4B)       Temporal            (Gemma 4 E4B       │
│                      Modeling            Thinking Mode)     │
└─────────────────────────────────────────────────────────────┘
```

| Component | Full Name | Role |
|-----------|-----------|------|
| **IRIS** | Integrated Reality Intelligence Scanner | Sensory — sees the world |
| **LOCUS** | Local Observation & Community Understanding System | Memory — remembers the world |
| **PRAXIS** | Predictive Resilience & Adaptive Expert Intelligence System | Reasoning — acts on the world |

---

## Core Features

| Feature | Description |
|---------|-------------|
| Crop Health Analysis | Multimodal disease diagnosis across 15+ staple crops with differential reasoning |
| Soil Intelligence | Visual soil health assessment — organic matter, compaction, erosion, drainage |
| Water Quality Monitoring | Surface water visual risk classification across four safety categories |
| Deforestation Tracking | Sequential photograph comparison for land-use change detection |
| Acoustic Monitoring | Passive audio analysis for pest detection and water flow assessment |
| Longitudinal Memory | Multi-season knowledge graph that compounds value over time |
| Traditional Knowledge | Indigenous ecological knowledge held as first-class alongside agronomic science |
| Proactive Alerts | Unprompted threat and opportunity detection from pattern accumulation |
| Seasonal Forecasting | Three-tier forecasting: trajectory, yield projection, next-season preparation |
| Voice Interface | Full voice-first interaction in 140+ languages with code-switching support |
| Community Mesh Sync | Multi-device model synchronization over Wi-Fi Direct / Bluetooth — no internet |
| PSALM Integration | Inter-community data sharing with ZK-proof provenance via libp2p mesh |
| Carbon Tracking | Ecosystem service documentation for voluntary carbon markets |
| Market Intelligence | Locally-cached price data for resource-aware recommendations |
| Regional Hub | Gemma 4 26B MoE deployed at community connectivity points for deeper analysis |

---

## Technical Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| Primary Reasoning | Gemma 4 E4B (Unsloth-tuned, Q4 GGUF) | IRIS + PRAXIS inference engine |
| Regional Hub | Gemma 4 26B MoE | Deep regional pattern analysis |
| On-Device Runtime | LiteRT (GPU/NPU acceleration) | Qualcomm Hexagon / MediaTek APU |
| Platform | Kotlin / Compose Multiplatform | Android primary, Raspberry Pi hub |
| Knowledge Graph | SQLite-embedded graph store | LOCUS ecological memory |
| Local Cache | Encrypted Hive + Room DB | Observations, media, knowledge library |
| Mesh Sync | Wi-Fi Direct + Bluetooth LE | Community model synchronization |
| PSALM Integration | libp2p via PSALM Cactus framework | Inter-community data sharing |
| Languages | Gemma 4 native 140+ | Voice and text interaction |

**Minimum device:** Android 3GB RAM
**Primary target:** Android 6–8GB RAM
**Hub hardware:** Raspberry Pi 5 (8GB RAM)

---

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture Overview](docs/architecture.md) | Trinity architecture — IRIS, LOCUS, PRAXIS data flow |
| [IRIS Specification](docs/iris.md) | Sensory layer — full multimodal perception breakdown |
| [LOCUS Specification](docs/locus.md) | Memory layer — knowledge graph and temporal modeling |
| [PRAXIS Specification](docs/praxis.md) | Reasoning layer — proactive intelligence and forecasting |
| [Extended Features](docs/features.md) | Voice, carbon tracking, market intelligence, regional hub |
| [Technical Stack](docs/technical-stack.md) | Full stack, device requirements, performance profiles |
| [Impact Model](docs/impact-model.md) | Impact indicators and measurement framework |
| [Deployment Guide](docs/deployment.md) | Android and Raspberry Pi hub deployment |

---

## Design Principles

**Sovereignty First.** No observation, no personal data, no community knowledge leaves the device without explicit community consent. MERIDIAN does not require connectivity. It does not phone home. Everything it knows about a community belongs to that community, stored encrypted on hardware they control.

**Accumulated Intelligence.** A single interaction with an AI tool is a transaction. MERIDIAN is a relationship. The system grows more valuable with every observation, every season, every outcome recorded. A community that has used MERIDIAN for three years has a system that understands their land in a way no cloud-based service ever could.

**Transparent Reasoning.** Every alert and recommendation MERIDIAN generates is accompanied by the full chain of evidence that produced it. The farmer sees not just what the system recommends but why. Trust is not assumed. It is built through legibility.

---

## Hackathon Context

MERIDIAN is submission **#4** from **Or4cl3 AI Solutions** for the [Gemma 4 Good Hackathon](https://www.kaggle.com/competitions/gemma-4-good-hackathon) (Kaggle × Google DeepMind).

| Detail | Value |
|--------|-------|
| Competition | Gemma 4 Good Hackathon |
| Track | Climate & Global Resilience |
| Prize Pool | $200,000 USD |
| Deadline | May 18, 2026 |
| Architect | ARSCIA — Or4cl3 AI Solutions |

The Or4cl3 lineup covers every major hackathon track:

| Project | Track | Focus |
|---------|-------|-------|
| LUMINA | Education / Digital Equity | Trauma-informed education for children in humanitarian crises |
| KINETIC | Health & Global Resilience | Emergency triage and infrastructure repair in zero-connectivity zones |
| PSALM | Safety & Trust / Digital Equity | Decentralized data sovereignty mesh with ZK-proof validation |
| **MERIDIAN** | **Climate & Global Resilience** | **Ecological intelligence for farming communities** |

---

## License

Licensed under the [Apache License 2.0](LICENSE) — the same license as Gemma 4 — enabling full commercial use, modification, and distribution with appropriate attribution.

---

<div align="center">

**Or4cl3 AI Solutions** | Gemma 4 Good Hackathon 2026

*Built with [Gemma 4](https://deepmind.google/models/gemma/gemma-4/) by Google DeepMind*

</div>
