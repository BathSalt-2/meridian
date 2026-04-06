# IRIS — Integrated Reality Intelligence Scanner

> The sensory layer. IRIS is how MERIDIAN sees and hears the physical world.

---

## Overview

IRIS converts raw environmental observation — photographs, audio recordings, and voice descriptions — into structured ecological intelligence. It is powered by Gemma 4 E4B's native multimodal stack, fine-tuned via Unsloth on a comprehensive agricultural and ecological corpus.

IRIS does not operate as a simple classifier. Every output includes a differential diagnosis, confidence scores for each hypothesis, the specific sensory features that drove the assessment, and contextual follow-up guidance. The farmer always sees the reasoning, not just the conclusion.

---

## Visual Crop Health Analysis

### Supported Crops
Maize, cassava, sorghum, millet, rice, wheat, beans, groundnuts, plantain, yam, potato, sweet potato, tomato, onion, cabbage, and the primary cash crops of Sub-Saharan Africa, South/Southeast Asia, and Latin America. The corpus is extensible through community contribution.

### Disease Detection Categories

| Category | Examples |
|----------|---------|
| Fungal | Leaf blight, powdery mildew, rust, anthracnose, damping-off |
| Bacterial | Wilting, canker, soft rot, bacterial leaf spot |
| Viral | Mosaic virus, cassava brown streak, maize streak, leaf curl |
| Pest Damage | Fall armyworm feeding, aphid colonies, stem borer tunneling, mite damage |
| Abiotic Stress | Nutrient deficiencies, drought stress, waterlogging, sunscald |

### Analysis Protocol
1. **Species Identification** — Crop type and growth stage classification
2. **Anomaly Detection** — Identification of departures from healthy baseline
3. **Differential Diagnosis** — Multi-hypothesis assessment with confidence scores
4. **Severity Staging** — Five-stage scale calibrated to intervention urgency
5. **Evidence Citation** — Specific visual features driving each hypothesis
6. **Follow-Up Prompting** — Targeted additional photographs to resolve ambiguity

### Example Output Structure
```
Primary Finding: Cassava Brown Streak Disease (Confidence: 82%)
Evidence: Yellowing in interveinal pattern (leaves 3-5), brown
          necrotic streaking visible in photographed stem cross-section.

Alternative: Zinc Deficiency (Confidence: 14%)
Evidence: Similar interveinal chlorosis pattern.
Distinguishing Test: Photograph newest growth tips — CBSD leaves
                     tips green; zinc deficiency affects tips first.

Alternative: Iron Deficiency (Confidence: 4%)
Evidence: Minimal — no yellowing on young leaves.

Severity: Stage 2 of 5 — Early-moderate. Addressable.
Recommended Action: [Routes to PRAXIS for resource-aware protocol]
```

---

## Soil Intelligence

Soil analysis from smartphone photography is inherently approximate — IRIS is explicit about this in every soil assessment output. The system provides structured visual indicators for decision-making, not laboratory precision.

### Assessment Parameters

| Parameter | Visual Indicators | Accuracy Note |
|-----------|-----------------|---------------|
| Organic Matter | Soil color (Munsell digital comparison), surface structure | Approximate — confirms via physical smell test guidance |
| Compaction | Surface crack patterns, ponding indicators, root visibility | Moderate accuracy |
| Drainage | Color mottling (grey/orange), surface texture, crack patterns | Good for chronic conditions |
| Clay-to-Sand Ratio | Particle size in close-up macro images | Approximate — recommends jar-shake confirmation |
| Erosion Severity | Rill and gully morphology, aggregate stability visible | Good accuracy |
| Surface Crust | Reflectance pattern, seedling emergence obstruction | Good accuracy |

### Guided Photography Protocol
IRIS provides on-screen framing guides and lighting prompts to standardize soil photographs for analysis. Without standardization, soil color assessment is unreliable due to variable lighting conditions.

---

## Water Quality Visual Assessment

IRIS classifies surface water sources into four risk categories based on visual inspection:

| Category | Visual Indicators | Guidance |
|----------|-----------------|----------|
| Low Risk | Clear, minimal turbidity, clean banks, no surface film | Safe for crop irrigation and livestock |
| Moderate Risk | Mild turbidity, some bank disturbance | Irrigation with caution; livestock monitoring advised |
| Elevated Risk | Heavy turbidity, surface film, algae bloom, upstream activity | Identify contamination type; basic field testing recommended |
| High Risk | Discoloration (grey/green/orange), strong odor indicators, dead vegetation on banks | Do not use without treatment — specific concern flagged |

For elevated and high-risk assessments, IRIS generates a probable contamination hypothesis (agricultural runoff, industrial, sewage, natural mineral) based on visual characteristics and recommends specific low-tech field confirmation tests.

---

## Deforestation and Land-Use Change Detection

IRIS supports landscape-scale ecological monitoring through sequential photograph comparison at consistent vantage points.

### Methodology
- Community members photograph defined landscape transects at monthly intervals
- IRIS compares sequential images to detect vegetation cover changes
- Change classification: Natural disturbance / Anthropogenic clearing / Regeneration / Stable
- Rate estimation: Area cleared per time period (approximate, calibrated against known reference distances)

### Integration with LOCUS
All change detections are written to LOCUS as Place-anchored Observation nodes with change magnitude, direction, and classification. PRAXIS uses this time series to generate deforestation trend alerts and carbon tracking updates.

---

## Acoustic Environmental Monitoring

Gemma 4 E4B's native audio input enables passive environmental sound analysis without requiring active user engagement.

### Detection Capabilities

| Signal | Species/Condition | Confidence Notes |
|--------|------------------|------------------|
| Wing-beat frequency | Fall armyworm moths, whitefly, aphid flight activity | Good for high-density infestations |
| Stridulation | Crickets, grasshoppers (presence/density estimate) | Moderate |
| Water flow turbulence | Stream volume and flow rate estimation | Good for large flow changes |
| Livestock vocalization | Distress indicators, respiratory abnormality | Moderate — flags for visual follow-up |

### Passive Monitoring Mode
During field walks, the device can be placed in a crop row or near a water source in passive acoustic mode. IRIS logs a 30-second acoustic sample every 5 minutes, analyzes it against the trained acoustic corpus, and flags anomalous signatures for farmer review at session end.

---

## Guided Observation Protocol

IRIS does not require farmers to know what to observe. At the start of each monitoring session, the system generates a contextual checklist based on:

- Current growth stage of each registered crop
- Season position relative to LOCUS historical patterns
- Active PRAXIS alerts and their associated monitoring priorities
- Regional threat intelligence from PSALM mesh
- Time since last observation of each field/water source

The checklist presents specific, actionable tasks: which fields to visit, what to look for, which surfaces to photograph, where to hold the microphone. This transforms ad hoc farm walks into systematic ecological monitoring without requiring agronomic training.

---

## Fine-Tuning Details

The base Gemma 4 E4B model is domain-adapted via Unsloth using:

- Agricultural image classification datasets (crop disease photo libraries, PlantVillage, CGIAR research image archives)
- Soil survey photography archives (FAO, national soil surveys)
- Pest and disease audio signature libraries
- Regional crop variety morphology documentation
- Deforestation satellite-to-ground calibration datasets

Fine-tuning uses Unsloth Dynamic GGUF 2.0 with layer-specific bit allocation — critical inference layers retain Q8 precision; embedding and stable layers are compressed to Q4. This achieves near-BF16 accuracy at mobile-compatible memory footprint.
