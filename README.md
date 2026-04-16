# Institute IMS — DASS Assignment 3 (prototype)

Native Android prototype built in **Jetpack Compose** with **fake local data** (no backend).

## Selected modules

1. **Dashboard** — role-aware hub with quick search, summary stats, module cards, latest news  
2. **Student Details** — student directory with batch/status filters, advanced filters, profile drill-down  
3. **Examinations** — exam list, create exam, exam detail/results, reports & analytics (stats + bucket bars)

## Tech stack

- Kotlin, Jetpack Compose, Material 3, Navigation Compose  
- ViewModel + StateFlow UI state  
- No Room / Hilt / Koin / Dagger; no XML layouts for screens

## Architecture (short)

- `MainActivity` hosts `ImsNavHost` inside `ImsTheme`  
- UI by feature under `app/src/main/java/com/institute/ims/ui/`  
- Repositories are in-memory singletons (`Fake*Repository`) behind small interfaces in `data/repository/`  
- Models in `data/model/`, helpers in `utils/`

## Implemented features by module

| Module | What works |
|--------|------------|
| Dashboard | Greeting header, role-aware quick stats, quick search, module entry points (Students/Exams), latest news list + search |
| Student Details | Batch chips, status chips (current/former), advanced filters panel, search, clean empty states, open student profile |
| Examinations | Group filter, exam cards with metadata, create exam flow, exam detail + results/empty state, report screen with avg/high/low/pass%, bucket-bar distribution, empty analytics handling |

Seeded data includes an exam **without** results to demo empty report sections.

## How to run

1. Open the project root in **Android Studio**.  
2. Run the **`app`** configuration on an emulator/device (**minSdk 26**).  
3. Build check: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).

## Suggested viva walkthrough

1. Splash → select user (Admin/Faculty) → Dashboard  
2. Students → filter/search → open profile → back  
3. Examinations → open an exam with results → Report → show stats + distribution  
4. Open **Database Systems — Viva** (no results) → empty analytics + preview chart  
5. Create new exam → save → verify it appears in the list

## Fake repositories / no backend

All domain data is stored in-memory in fake repositories for demo purposes (users, students, exams, news, dashboard stats). No REST API or database is used for these features.
