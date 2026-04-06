# MERIDIAN — Extended Features

Beyond the Trinity Architecture (IRIS, LOCUS, PRAXIS), MERIDIAN includes several integrated systems that extend its utility across the full range of community needs.

---

## Voice-First Interface

MERIDIAN's primary interaction mode is not text entry. In many target communities, literacy rates are variable and touchscreen text input is unfamiliar or uncomfortable. The entire MERIDIAN interface is accessible through natural language voice.

### Capabilities
- Full voice navigation through all MERIDIAN features
- Natural voice input for IRIS observation descriptions
- PRAXIS recommendations delivered in spoken form
- Voice-guided IRIS observation protocols ("Hold the phone 30cm from the leaf. Good — now rotate to show the underside.")
- Voice-recorded community notes attached to any LOCUS node
- 140+ language support via Gemma 4's native multilingual audio encoder
- Code-switching support — mixed-language input within a single utterance is handled gracefully

### Accessibility Design
- Minimum touch: initiating and ending sessions only
- All screen content mirrored in audio
- Adjustable speaking pace
- High-contrast visual mode for bright outdoor conditions
- Large-touch interface elements for use with work-roughened hands

---

## Carbon and Ecosystem Service Tracking

For communities engaged in or seeking access to voluntary carbon markets, reforestation programs, or payment-for-ecosystem-services (PES) schemes, MERIDIAN includes a dedicated ecological service documentation module.

### Tracked Indicators
- Forest cover change rate (from IRIS deforestation monitoring)
- Soil organic matter trajectory (from IRIS soil assessments over time)
- Water retention capacity indicators (from water source observations)
- Biodiversity indicators (species presence/absence from acoustic monitoring and visual identification)
- Agroforestry canopy coverage estimates

### Documentation Output
The module generates structured reports in formats compatible with Verra VCS, Gold Standard, and Plan Vivo voluntary carbon methodologies. Reports draw on the LOCUS historical record to demonstrate:
- Baseline ecological conditions at program start
- Trajectory of change under community management
- Attribution of change to community practices

### PSALM Provenance Integration
All carbon documentation reports are certified through PSALM's ZK-proof ledger. The ZK certification allows external verifiers to confirm the report's data provenance and tamper-evident integrity without requiring a physical audit visit or gaining access to raw community data.

A community with three years of MERIDIAN records and PSALM-certified carbon documentation has a verifiable, externally-auditable proof of ecological stewardship that can support carbon credit claims, biodiversity payments, and watershed service payments — all without depending on an external auditor's physical presence.

---

## Agricultural Knowledge Commons

### Embedded Offline Library
MERIDIAN ships with ~800MB of curated offline agricultural reference content:
- Crop agronomy guides for all supported crop types
- Integrated pest and disease management encyclopaedia
- Soil health and fertility management guides
- Water management and conservation techniques
- Climate adaptation practices by region and farming system
- Seed saving and variety selection guides
- Post-harvest handling and storage protocols

The library is queryable through PRAXIS (it draws on it for recommendations) and browsable directly by farmers through a guided search interface accessible by voice.

### Community Knowledge Contributions
When a farmer records a successful intervention and its outcome in LOCUS, they can contribute a structured practice write-up to the community's local knowledge library section:
- What they did
- Under what conditions (season, crop stage, resource availability)
- What the outcome was
- Whether they would recommend it

Community-contributed entries are stored in LOCUS, shared across community devices via mesh sync, and optionally shared to the PSALM regional network for neighboring communities — with the contributor's explicit consent and fully anonymized.

---

## Market Intelligence Module

Agricultural decisions are economic decisions. PRAXIS recommendations need price context to be actionable.

### Price Tracking
Community members report current prices at the nearest accessible market through a simple three-tap interface when returning from market visits. Prices tracked:
- Primary crop sale prices (per kg or local unit)
- Key input prices (fertilizer, seed, pesticide)
- Transport cost to market
- Market availability of specific inputs

### Economic Integration with PRAXIS
PRAXIS uses current market price data to calculate the economic rationale for recommended interventions:
- Estimated intervention cost vs. estimated crop value protected
- Break-even analysis for recommended inputs
- Comparative economics of intervention options

PRAXIS will not recommend a $15 treatment for a crop worth $8 at current market prices without flagging the economic rationale explicitly. The recommendation is still presented — the farmer may have strategic reasons to protect the crop beyond immediate market value — but the economic context is always surfaced.

### Community Price Network
Price data is shared across community devices via mesh sync. A single farmer's market visit updates the price model for all community members. A community sending one member to market per week maintains near-current price data across all devices at no additional cost.

---

## Community Onboarding Journey

MERIDIAN's onboarding is a multi-session structured engagement, not a user agreement and tutorial.

### Session 1 — Community Mapping (90–120 minutes)
- Define the community landmark grid (establish spatial reference system)
- Register primary fields, water sources, forest patches, and grazing areas as Place nodes
- Photograph each place for the LOCUS baseline record
- Establish the community resource profile (available inputs, market access, labor capacity)

### Session 2 — Traditional Knowledge Capture (120–180 minutes, ideally as community gathering)
- Guided elder knowledge interview protocol
- Ecological calendar encoding (planting triggers, seasonal indicators)
- Traditional variety catalogue (names, characteristics, management practices)
- Traditional pest and disease knowledge
- Sacred, restricted, or ecologically sensitive areas
- Traditional water management practices

### Session 3 — Practical Training (60–90 minutes)
- IRIS observation protocol walkthrough in the field
- First real observation session with IRIS guidance
- LOCUS data review — confirming the map and graph look correct
- PRAXIS alert system explanation — what triggers an alert, how to read it, how to respond
- Multi-device sync demonstration (if multiple devices are in use)

After these three sessions, MERIDIAN has enough foundational knowledge to provide meaningful, calibrated guidance from the very first IRIS observation. The system is useful immediately.

---

## Regional Hub

### Hardware
Raspberry Pi 5 (8GB RAM) running Gemma 4 26B MoE at Q4 quantization. Deployed at fixed community infrastructure points with intermittent connectivity: regional health posts, mission stations, market town anchor buildings.

### Functions
- Aggregate anonymized LOCUS data from multiple communities via PSALM mesh
- Generate regional threat models and outbreak progression maps
- Download updated knowledge library packages during connectivity windows
- Push IRIS calibration updates to edge devices (new disease variants, regional crop condition updates)
- Provide deeper strategic analysis that exceeds E4B's on-device reasoning capacity

### Community Interaction
When a community member's device comes within range of a hub (during a market visit, for example), automatic sync occurs:
1. Anonymized community LOCUS data uploads to hub
2. Updated knowledge library packages download to device
3. Regional intelligence alerts download to device
4. IRIS calibration updates download to device

The sync is automatic and silent. No action required from the farmer beyond proximity to the hub.
