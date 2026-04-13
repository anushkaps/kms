# Institute IMS — DASS Assignment 3 (prototype)

Jetpack Compose Android app demonstrating an **Institute Management System** slice with **fake local data** (no backend for domain features).

## Selected modules

1. **Dashboard** — role-aware home, stats, module entry points, searchable news  
2. **Student Details** — directory, filters, student profile  
3. **Examinations** — exam list, create exam, detail, **reports & analytics**

## Tech stack

- Kotlin, **Jetpack Compose**, **Material 3**, Navigation Compose  
- **ViewModel** + **StateFlow** for UI state  
- **OkHttp** — optional one-shot startup request only (see below)  
- **No** Room, Hilt, Koin, Dagger, or XML layouts for screens  

## Architecture (short)

- **Single activity**: `MainActivity` hosts `ImsNavHost` inside `ImsTheme`  
- **UI** under `app/.../ui/` by feature (`dashboard`, `studentdetails`, `examinations`, `common`)  
- **Contracts** in `data/repository/*.kt`; **implementations** are `Fake*` in-memory singletons  
- **Models** in `data/model/`  
- Shared helpers in `utils/` (e.g. grade labels, exam analytics, startup tracker)  

## Implemented features by module

| Module | What works |
|--------|------------|
| **Dashboard** | Pick user role, greeting, quick stats, “Today” line, module cards → Students / Exams, news list with quick search |
| **Student Details** | Batch & status chips, advanced filters, search, list → profile (contact, batch, programme metadata) |
| **Examinations** | Group filter, exam cards, FAB create exam, detail with results, app-bar **Report** → averages, pass rate (40% of max marks), bucket bars, empty states |

Seeded data includes an exam **without** results to demo empty analytics.

## How to run

1. Open the project root in **Android Studio** (recent stable; AGP/Gradle as in repo).  
2. Run the **`app`** configuration on an emulator or device (**minSdk 26**).  
3. Build verification: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).

## Suggested viva walkthrough

1. **Splash** → **Choose user** (Admin / Faculty) → **Dashboard**.  
2. **Students** → adjust filters / search → open a student → **Back**.  
3. **Examinations** → change group filter → open an exam with results → **Report** → point out stats + distribution.  
4. Open an exam **without** results (seed: **Database Systems — Viva**) → empty analytics + preview chart.  
5. **New exam** → save → confirm it appears in the list.  
6. State clearly: **all IMS data is fake/in-memory**; persistence is not the goal of this assignment build.

## Fake repositories / no backend

Domain data (users, students, exams, news, dashboard copy) lives in **`FakeUserRepository`**, **`FakeStudentRepository`**, **`FakeExamRepository`**, **`FakeNewsRepository`**, **`FakeDashboardRepository`**. There is **no** REST API or database for these features.

## `APP_IDENTIFIER` (startup)

`BuildConfig.APP_IDENTIFIER` is posted **once on cold start** in a **background thread** if the value is non-empty. This does not drive app logic; it is only for external assignment tracking.
