
# TrailReview – Starter Pack

This pack gives you the *baseline repo files* you asked for. Unzip into the **root** of your Android Studio project (the folder that contains `app/`), then commit and push.

## Contents
- `.github/workflows/android-ci.yml` – GitHub Actions CI (build, unit tests, lint)
- `.github/pull_request_template.md` – PR checklist
- `.gitignore` – Android ignores
- `app/src/main/assets/trails.json` – seed trails (Sprint 1 demo)
- `app/src/main/java/edu/vt/trailreview/util/JsonLoader.kt` – tiny helper to load `trails.json`

## Quick Start
1) **Copy files** into your project root (merge folders).
2) **Commit & push**:
   ```bash
   git add .github .gitignore app/src/main/assets/trails.json app/src/main/java/edu/vt/trailreview/util/JsonLoader.kt README.md
   git commit -m "chore: add CI, PR template, ignores, and trails seed"
   git push
   ```
3) **Show pins from JSON** (Sprint 1):
   - Call `JsonLoader.loadTrails(context)` from your Map screen (e.g., Fragment).
   - For each returned `TrailSeed`, add a marker.

## Kotlin snippet (Map screen usage)

```kotlin
// Example usage inside your Map Fragment (pseudo-code)
val seeds = JsonLoader.loadTrails(requireContext())
for (t in seeds) {
    // addMarker(lat = t.lat, lng = t.lng, title = t.name)
}
```

## Definition of Done (Sprint 1)
- App builds & runs from Android Studio
- Map renders with 3–5 pins from JSON
- Profile screen accessible (static username/avatar)
- CI (build, test, lint) is green on PRs

---

### Tip: PR Workflow
- Feature branches: `feature/<area>-<desc>`
- Open PR → fill template → get 1 approval → merge when CI passes
