# MERIDIAN — Trinity Architecture

> Three cognitive layers. One living ecological intelligence.

---

## Overview

MERIDIAN is structured as three tightly coupled but independently scoped cognitive layers — IRIS, LOCUS, and PRAXIS — each responsible for a distinct phase of the perception-memory-reasoning cycle. This separation ensures that each layer can be tested, improved, and scaled independently without disrupting the overall system.

The architecture is explicitly recursive: PRAXIS reasoning outputs feed back into LOCUS as structured outcomes, which in turn refine the context available to future IRIS observations. The system gets smarter with every cycle.

---

## The Data Flow

```
  Physical World
       │
       ▼
  ┌─────────┐     Structured Observation
  │  IRIS   │ ──────────────────────────────▶ ┌─────────┐
  │         │                                 │  LOCUS  │
  │ Sensory │ ◀──────────────────────────────  │         │
  │  Layer  │     Contextual Observation Cues  │  Memory │
  └─────────┘                                 │  Layer  │
       ▲                                      └─────────┘
       │                                           │
  User Action                            Knowledge Graph
  Community Alert                                  │
       │                                           ▼
       │                                    ┌─────────────┐
       └────────────────────────────────────│   PRAXIS    │
                  Recommendations           │             │
                  Proactive Alerts          │  Reasoning  │
                                            │   Layer     │
                                            └─────────────┘
                                                  │
                                                  ▼
                                          Outcomes → LOCUS
                                          (Feedback Loop)
```

---

## Layer 1 — IRIS (Integrated Reality Intelligence Scanner)

**Role:** Sensory layer. Converts physical-world observations into structured ecological data.

**Primary Technology:** Gemma 4 E4B multimodal inference (vision + audio + text)

**Inputs:**
- Camera images (crops, soil, water, landscapes, deforestation)
- Microphone audio (pest sounds, water flow, livestock)
- User voice descriptions
- Guided observation protocol responses

**Outputs:**
- Classified observations with confidence scores
- Differential diagnoses with evidence chains
- Structured data objects written to LOCUS
- Contextual follow-up observation prompts

**Key Principle:** IRIS never returns a single collapsed answer. Every output includes the reasoning chain, alternative hypotheses with confidence scores, and the specific visual or acoustic features that drove the classification. Transparent reasoning is a design requirement, not a feature.

→ [Full IRIS Specification](iris.md)

---

## Layer 2 — LOCUS (Local Observation and Community Understanding System)

**Role:** Memory layer. Maintains the living ecological model of the community's land.

**Primary Technology:** SQLite-embedded graph database, encrypted at rest

**Data Model:**

| Node Type | Description |
|-----------|-------------|
| Place | Geographic anchors — fields, water sources, forest patches (landmark-grid, not GPS) |
| Observation | Timestamped IRIS outputs anchored to places |
| Crop/Species | Biological subjects — varieties, pests, pathogens, wild species |
| Season/Event | Temporal structure — planting windows, rainfall events, historical markers |
| Intervention | Actions taken in response to observations or recommendations |
| Outcome | Results of interventions — closes the feedback loop |

**Key Capabilities:**
- Within-season trajectory tracking
- Year-over-year pattern comparison
- Multi-year trend detection (slow-onset climate signals)
- Traditional knowledge encoding and retrieval
- Multi-device community model synchronization (mesh)
- PSALM-compatible anonymized export

→ [Full LOCUS Specification](locus.md)

---

## Layer 3 — PRAXIS (Predictive Resilience and Adaptive Expert Intelligence System)

**Role:** Reasoning layer. Transforms accumulated LOCUS data into proactive, actionable intelligence.

**Primary Technology:** Gemma 4 E4B with native Thinking Mode enabled

**Alert Categories:**

| Alert Type | Trigger |
|------------|--------|
| Threshold Alert | Measurable indicator crosses a critical boundary |
| Pattern Divergence | Current season deviates from historical LOCUS patterns |
| Regional Intelligence | PSALM mesh data indicates incoming regional threat |
| Opportunity Alert | Conditions favorable for high-value intervention |

**Reasoning Protocol:** Every PRAXIS output is generated through Gemma 4's native thinking mode. The reasoning chain is exposed to the user — not just the conclusion, but the evidence, the competing hypotheses, the confidence level, and the conditions that would revise the assessment.

**Feedback Loop:** Intervention outcomes recorded by the community are written back to LOCUS as Outcome nodes, continuously refining the accuracy of future PRAXIS recommendations for this specific community's conditions.

→ [Full PRAXIS Specification](praxis.md)

---

## Cross-Layer Interactions

### IRIS → LOCUS
Every IRIS observation is immediately structured into an Observation node and written to LOCUS, anchored to the appropriate Place node and timestamped. The LOCUS model updates in real time as IRIS processes new data.

### LOCUS → IRIS
LOCUS provides IRIS with contextual priming before each observation session. The current season, recent PRAXIS alerts, and gap analysis from the knowledge graph inform IRIS's guided observation protocol — determining what the system asks the farmer to photograph next.

### LOCUS → PRAXIS
PRAXIS queries LOCUS continuously using the knowledge graph's temporal and relational structure. Pattern divergence detection, trend analysis, and seasonal forecasting all draw on the full LOCUS model, not just recent observations.

### PRAXIS → LOCUS
When PRAXIS generates an alert, the alert itself becomes a LOCUS node — timestamped, linked to the triggering observations, and subsequently linked to the Intervention and Outcome nodes that follow. This creates a complete audit trail: what the system detected, when, why, what was done, and what resulted.

---

## Security and Privacy Architecture

All LOCUS data is encrypted at rest using AES-256. The encryption key is derived from a community-set PIN and never stored on the device — it is derived at runtime. Data synchronization between community devices uses end-to-end encrypted channels over local mesh (no data transits external networks). PSALM export packages are anonymized and ZK-proof certified before transmission — the receiving community can verify provenance without knowing the source community's identity.

---

## Regional Hub Extension

For communities with intermittent connectivity at a fixed location, an optional Regional Hub tier extends the architecture. The hub runs Gemma 4 26B MoE on Raspberry Pi 5 hardware, providing substantially deeper reasoning for strategic analysis, and acts as an aggregation point for anonymized PSALM data from multiple communities in a region.

The hub does not replace the on-device architecture. It augments it. Individual device functionality remains complete and independent at all times.
