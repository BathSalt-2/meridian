# PRAXIS — Predictive Resilience and Adaptive Expert Intelligence System

> The reasoning layer. PRAXIS is how MERIDIAN thinks, anticipates, and acts.

---

## Overview

PRAXIS is the intelligence core of MERIDIAN. It transforms the accumulated ecological memory held by LOCUS into proactive, actionable guidance — not in response to questions alone, but autonomously, when patterns in the data indicate that a community's attention is warranted.

PRAXIS is powered by Gemma 4 E4B's native Thinking Mode. Every substantive output — alert, recommendation, forecast — is generated through a transparent reasoning chain that the farmer can read, evaluate, and challenge. The goal is not compliance with PRAXIS outputs. It is informed decision-making supported by PRAXIS outputs.

---

## The Proactive Alert Engine

PRAXIS monitors the LOCUS knowledge graph continuously and fires reasoning processes when trigger conditions are met.

### Alert Categories

#### Threshold Alerts
Fire when a tracked indicator crosses a threshold calibrated to crop-stage-specific critical values.

Examples:
- Soil moisture indicators below crop-stage minimum for three consecutive observation periods → irrigation urgency assessment
- IRIS disease severity staging reaching Stage 3 → escalation protocol generation
- Water source visual risk classification elevating → alternative source assessment
- Acoustic pest density estimate crossing infestation threshold → field scouting protocol

#### Pattern Divergence Alerts
Fire when the current season's trajectory deviates significantly from LOCUS historical patterns at the same point in previous seasons.

Examples:
- Rainfall onset indicators absent beyond the historically latest date → delayed-season protocol
- Crop growth stage running two weeks behind historical average → stress diagnosis and intervention options
- Planting outcomes tracking below all previous seasons at equivalent stage → root cause analysis

#### Regional Intelligence Alerts
Fire when PSALM mesh data from neighboring communities indicates an approaching regional threat.

Examples:
- Fall armyworm outbreak reported in windward communities → pre-emptive monitoring intensification
- Disease variant spreading across regional PSALM network → early detection protocol activation
- Drought conditions spreading from neighboring community reports → water conservation planning trigger

#### Opportunity Alerts
Fire when conditions are particularly favorable for high-value interventions.

Examples:
- Optimal soil moisture window for deep-rooted legume establishment
- Pest pressure at seasonal minimum — ideal window for new variety establishment
- Extended growing season indicators suggesting second crop cycle feasibility
- Favorable rainfall trajectory enabling an irrigation infrastructure investment

---

## The Thinking Mode Reasoning Engine

Every PRAXIS output is generated through Gemma 4 E4B's native thinking mode. The model reasons step-by-step before committing to an output, and MERIDIAN exposes the full reasoning chain to the farmer.

### Output Structure

```
[PRAXIS ALERT — Pattern Divergence]

What I observed:
  Rainfall onset indicators (Acacia flowering, Bradypterus locustella
  call frequency) have not been detected as of Day 47 of the season.
  In your LOCUS record, the latest rainfall onset across 4 previous
  seasons was Day 38. Current deviation: +9 days beyond historical maximum.

What pattern I identified:
  Comparing current season trajectory against your 4 recorded seasons,
  the closest match is Long Rains 2022 — which also showed delayed onset.
  That season resulted in 23% below-average yield on your maize fields
  and normal yield on sorghum (which tolerates delayed planting better).

What this suggests:
  High probability (74%) of shortened effective growing season.
  This affects planting decisions for the next 7 days.

What I am uncertain about:
  Whether the delay is a one-off anomaly (as in 2022) or part of a
  multi-year shift in onset timing. I have 4 seasons of data. More
  seasons would improve this assessment.

Your options:
  Option 1 — Proceed with planned maize planting, accepting elevated
    yield risk. Resource cost: baseline. Risk: moderate.
  Option 2 — Switch eastern field to short-duration maize variety
    (Katumani, if available). Resource cost: seed procurement.
    Risk: lower if variety accessible.
  Option 3 — Shift eastern field to sorghum this season. Resource
    cost: seed swap. Risk: lowest yield variance, lower upside.

What would change this recommendation:
  If rainfall indicators appear within the next 5 days, revert to
  standard protocol and dismiss this alert.
```

This level of transparency allows the farmer to identify cases where PRAXIS has missed local context — for example, if the community knows a reason why the Acacia flowering indicator is unreliable this year. The farmer is not required to follow PRAXIS. They are equipped to evaluate it.

---

## Seasonal Forecasting

### Tier 1 — Current-Season Trajectory
Updated continuously as new observations arrive. Presents:
- Most likely end-of-season scenario
- Conditions that would improve the trajectory
- Conditions that would worsen it
- Specific monitoring flags for the coming two weeks

### Tier 2 — End-of-Season Yield Projection
Generated at 60% of the way through the growing season (when sufficient data exists for meaningful projection). Presents:
- Probability distribution of yield outcomes (most likely, optimistic, pessimistic)
- Primary factors driving the projection
- Remaining interventions that could shift the distribution
- Implications for post-harvest planning (storage, selling, consumption)

### Tier 3 — Next-Season Preparation Guidance
Generated in the final quarter of each season and updated through the dry season. Draws on the full current-season LOCUS record to produce:
- What went well and why
- What underperformed and the primary drivers
- Recommended soil amendments (prioritized by impact-per-effort)
- Crop variety selection guidance for next season
- Infrastructure improvements ranked by resilience impact
- Planting calendar adjustments based on observed onset pattern

---

## Resource-Aware Recommendation Engine

PRAXIS recommendations are filtered through the community's resource profile, established during onboarding and updated as the community's situation changes.

### Resource Profile Components
- Locally available inputs (organic amendments, tools, water sources)
- Nearest market access (frequency, transit time, available inputs)
- Community labor availability by season
- Financial capacity indicators (qualitative)
- Previous intervention track record (from LOCUS Outcome nodes)

### Filtering Logic
Every recommendation is generated in three tiers:
1. **Optimal** — best agronomic outcome regardless of resource constraints (shown for awareness)
2. **Achievable** — best outcome within available resources (primary recommendation)
3. **Minimal** — lowest-resource intervention that prevents worst-case outcome (emergency fallback)

PRAXIS leads with the Achievable tier. The Optimal tier is shown with a clear note when the required resources are not available locally, so the community can decide whether to seek them specifically for high-stakes situations.

---

## Feedback Loop

PRAXIS closes the cognitive loop by writing its outputs back into LOCUS as structured nodes:

1. Alert generated → Alert node created in LOCUS (linked to triggering observations)
2. Community acts → Intervention node created (linked to Alert)
3. Outcome assessed → Outcome node created (linked to Intervention)
4. PRAXIS learns → Outcome data refines confidence calibration for future similar alerts in this community

Over time, PRAXIS recommendations become increasingly calibrated to the specific soil conditions, microclimate, traditional practices, and community capacity of each unique community — a degree of specificity that no generic agricultural advisory service can match.
