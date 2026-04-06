# LOCUS — Local Observation and Community Understanding System

> The memory layer. LOCUS is how MERIDIAN remembers, organizes, and understands what it has seen.

---

## Overview

LOCUS maintains an encrypted, on-device knowledge graph representing the community's ecological reality. It is not a database of observations. It is a living model — one that builds richer understanding as relationships between places, species, seasons, interventions, and outcomes accumulate over time.

The key property that separates LOCUS from a simple log is **temporal intelligence**: the ability to reason not just about what has happened, but about how current conditions compare to historical patterns, and what those comparisons predict about what comes next.

---

## Knowledge Graph Schema

### Node Types

#### Place
Geographic anchors for all observations. Places are defined using a community landmark grid — not GPS coordinates, which require cloud services and create privacy exposure.

```
Place Node:
  id: UUID
  name: String (community-assigned)
  landmark_anchor: String ("north of the baobab tree")
  type: Enum [field, water_source, forest_patch, grazing_area, infrastructure]
  area_estimate_ha: Float (optional)
  registered_at: Timestamp
  last_observed: Timestamp
```

#### Observation
Timestamped records of IRIS outputs anchored to places.

```
Observation Node:
  id: UUID
  place_id: FK → Place
  timestamp: Timestamp
  iris_category: Enum [crop_health, soil, water, deforestation, acoustic, manual]
  classification: String
  confidence: Float (0.0 - 1.0)
  severity: Int (1-5, where applicable)
  evidence_chain: JSON (IRIS reasoning output)
  media_thumbnail: Blob (compressed)
  community_notes: String (optional)
  device_id: UUID (for multi-device provenance)
```

#### Crop / Species
Biological subjects tracked by the community.

```
Species Node:
  id: UUID
  common_name: String
  local_names: Array<String> (community language names)
  scientific_name: String (optional)
  type: Enum [crop, pest, pathogen, beneficial, wild_plant, livestock]
  variety_notes: String (community-documented variety characteristics)
  traditional_knowledge: JSON (practices, indicators, relationships)
```

#### Season / Event
Temporal markers anchoring observations to the ecological calendar.

```
Season Node:
  id: UUID
  label: String ("Long Rains 2025", "Dry Season 2024")
  start_date: Date
  end_date: Date (estimated if in progress)
  rainfall_assessment: Enum [above_average, normal, below_average, drought]
  notes: String
  traditional_calendar_position: String (community reference)
```

#### Intervention
Actions taken by the community in response to observations or PRAXIS recommendations.

```
Intervention Node:
  id: UUID
  observation_id: FK → Observation (triggering observation)
  praxis_alert_id: FK → Alert (if PRAXIS-triggered)
  place_id: FK → Place
  timestamp: Timestamp
  action_type: String
  resources_used: Array<String>
  applied_by: String (optional — anonymized role if preferred)
  notes: String
```

#### Outcome
Results of interventions — closes the feedback loop into PRAXIS.

```
Outcome Node:
  id: UUID
  intervention_id: FK → Intervention
  assessed_at: Timestamp
  result: Enum [resolved, improved, unchanged, worsened]
  yield_impact_estimate: String (qualitative)
  resource_cost: String (qualitative)
  community_notes: String
  contributed_to_commons: Boolean
```

---

## Temporal Intelligence

### Within-Season Tracking
LOCUS tracks each registered crop through its growth stages, comparing current development against expected timelines derived from the community's own historical data and the embedded agronomic knowledge library.

Anomalies — early maturity (heat stress indicator), delayed flowering (water stress), stunted growth (soil compaction or nutrient deficiency) — are flagged as pattern divergences and routed to PRAXIS for assessment.

### Year-Over-Year Pattern Comparison
When IRIS generates a new observation, LOCUS executes a temporal query: retrieve all observations of this place, this crop, at this growth stage, across all previous seasons in the record. The query result is passed to PRAXIS as historical context — a time series showing what "normal" looks like at this point in the season for this specific community.

This historical context allows PRAXIS to distinguish genuine anomalies from expected seasonal variation, dramatically reducing false-positive alerts while improving detection of true departures from baseline.

### Multi-Year Trend Detection
LOCUS executes trend detection queries across the full observation history at configurable intervals (weekly during growing season, monthly in dry season). Trend signals include:

- Soil health trajectory (organic matter indicators improving or declining)
- Planting calendar drift (is the community shifting dates in response to rainfall change?)
- Yield outcome trajectory (productivity improving, stable, or declining under current practices?)
- Forest cover change rate (deforestation accelerating, stable, or reversing?)
- Water source reliability (seasonal drying occurring earlier, later, or at normal timing?)

These multi-year trends are the primary input for PRAXIS strategic guidance — longer-horizon recommendations about farming system adaptation, not just immediate interventions.

---

## Traditional Knowledge Integration

LOCUS treats traditional and indigenous ecological knowledge as a primary data source. During community onboarding, a structured knowledge capture session guides elder farmers through encoding their knowledge into the graph.

### Captured Knowledge Types

| Knowledge Type | Graph Representation |
|---------------|---------------------|
| Traditional planting calendars | Season nodes with ecological trigger annotations |
| Rainfall onset indicators | Species nodes linked to Season nodes ("plant when X bird calls") |
| Traditional variety characteristics | Species nodes with variety_notes and traditional_knowledge fields |
| Soil classification vocabulary | Place nodes annotated with community soil names and descriptions |
| Traditional pest/disease knowledge | Species nodes with traditional management practices |
| Intercropping and companion planting relationships | Species-to-Species edges with relationship type |
| Sacred or restricted land areas | Place nodes with access_constraint flags |

### Reasoning Integration
When PRAXIS generates a recommendation, LOCUS provides both agronomic knowledge (from the embedded library) and traditional knowledge (from the community graph). PRAXIS presents:
- Cases where both agree: high-confidence recommendation
- Cases where they diverge: both options presented with explicit reasoning about why they differ
- Cases where only traditional knowledge applies: recommendation explicitly sourced to community knowledge, flagged for community validation

---

## Multi-Device Community Synchronization

MERIDIAN is designed for multi-device deployment across an entire community. Each community member with a device runs an independent LOCUS instance that synchronizes with other devices over local mesh.

### Sync Protocol
1. Devices detect each other over Bluetooth LE or Wi-Fi Direct
2. Devices exchange LOCUS version vectors (Lamport timestamps per node type)
3. Nodes created since last sync are exchanged bidirectionally
4. Conflict resolution: timestamp + confidence score arbitration
5. Merged model written back to both devices
6. Sync event logged with participating device IDs

### Community Model Properties
- Any individual device holds the superset of all synced observations
- A community deploying MERIDIAN across 20 devices runs a 20-node distributed monitoring network
- No device is authoritative — the network is peer-to-peer
- Sync events are logged but individual device-to-community-member mappings are not stored in LOCUS (privacy)

---

## PSALM Mesh Export

When a community chooses to contribute to the regional intelligence network via PSALM:

1. LOCUS generates an anonymized export package
2. All individual identity fields are stripped
3. Place coordinates are aggregated to region-level (5km grid)
4. Species identifications and disease classifications are retained (the valuable signal)
5. The package is submitted to PSALM's ZK-proof ledger for provenance certification
6. Receiving communities see verified regional data, clearly labeled as external, in their LOCUS model

The community retains full control of what is shared and can revoke mesh participation at any time. Revocation does not affect data already contributed — ZK-proof records are immutable — but prevents future contributions.

---

## Storage and Encryption

| Data Type | Storage | Encryption |
|-----------|---------|------------|
| Knowledge graph | SQLite (on-device) | AES-256, key derived from community PIN |
| Media thumbnails | Hive binary store | AES-256, same key |
| Traditional knowledge entries | SQLite | AES-256, same key |
| Sync logs | SQLite | AES-256, same key |
| PSALM export packages | Temporary file | Encrypted in transit, deleted post-send |

The encryption key is derived at runtime from the community PIN using PBKDF2 with 100,000 iterations. It is never stored. If the PIN is lost, the data is unrecoverable — community data sovereignty includes the risk of loss without backup.

Backup options: community-controlled export to encrypted file on external storage (USB OTG or SD card) using the same AES-256 key.
