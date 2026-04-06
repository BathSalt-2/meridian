# Contributing to MERIDIAN

MERIDIAN is an open-source project built for communities at the intersection of climate vulnerability and AI exclusion. We welcome contributions that advance that mission.

---

## Ways to Contribute

### Agricultural Knowledge Corpus
The most impactful contribution for non-engineers. We are actively seeking:
- Crop disease image datasets (especially regional and local crop varieties underrepresented in global datasets)
- Traditional ecological knowledge documentation from specific regions
- Soil survey photography archives
- Pest and disease audio signature recordings
- Regional crop variety catalogues

If you have access to agricultural research data or community ecological knowledge, please open a Discussion or contact the maintainers.

### Language Support
MERIDIAN relies on Gemma 4's native 140+ language support. We are seeking contributors to:
- Validate MERIDIAN's UI translations in specific languages
- Contribute agricultural terminology glossaries for regional languages
- Test voice interface accuracy in target languages

### Code Contributions
Before contributing code, please:
1. Open an Issue describing the change you want to make
2. Wait for maintainer feedback before investing significant time
3. Fork the repository and create a branch from `main`
4. Write tests for your changes
5. Submit a Pull Request with a clear description

### Field Testing
If you work with agricultural communities in MERIDIAN's target regions and can facilitate field testing, we want to hear from you. Field feedback is the most valuable data we can receive.

---

## Code Standards

- Kotlin style: follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Run `./gradlew detekt ktlintCheck` before submitting a PR
- All new LOCUS node types require unit tests for the full CRUD cycle
- All new IRIS analysis categories require evaluation test cases
- PRAXIS reasoning changes require before/after comparison on the evaluation harness

---

## Ethical Guidelines

MERIDIAN handles sensitive community data. All contributors must:
- Never commit real community data of any kind to this repository
- Design features with data minimization as a first principle
- Consider privacy implications of any new data collection
- Ensure all AI-generated recommendations are presented with appropriate uncertainty
- Avoid designing features that could be used to extract community data without consent

---

## Hackathon Context

MERIDIAN is currently an active submission in the [Gemma 4 Good Hackathon](https://www.kaggle.com/competitions/gemma-4-good-hackathon) (deadline: May 18, 2026). During the hackathon period, the core team will prioritize submissions that directly advance the competition entry.

---

## License

By contributing to MERIDIAN, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE) that covers the project.

---

*Built for communities. Governed by communities. Or4cl3 AI Solutions.*
