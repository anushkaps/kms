# Institute IMS - DASS Assignment 3 (prototype)

Jetpack Compose Android app demonstrating an **Institute Management System** slice with **fake local data** (no backend for domain features).

## Selected modules

1. **Dashboard**: navigational hub: **hub search** (modules + students + exams + news + **language/region**), **Language & region** card (country, currency, time zone), role-specific stats, module cards, news  
2. **Student Details**: directory, filters, student profile  
3. **Examinations**: list, create (category + **assessment mode** + evaluation type), detail, **report center** with tabbed views  

## Tech stack

- Kotlin, **Jetpack Compose**, **Material 3**, Navigation Compose  
- **ViewModel** + **StateFlow** for UI state  
- **OkHttp**: optional one-shot startup request only (see below)  
- **No** Room, Hilt, Koin, Dagger, or XML layouts for screens  

## Architecture (short)

- **Single activity**: `MainActivity` hosts `ImsNavHost` inside `ImsTheme`  
- **UI** under `app/.../ui/` by feature (`dashboard`, `studentdetails`, `examinations`, `settings`, `common`)  
- **Contracts** in `data/repository/*.kt`; **implementations** are `Fake*` in-memory singletons  
- **Models** in `data/model/`; shared pick-lists in `data/catalog/`  
- Shared helpers in `utils/` (e.g. grade labels, exam analytics, startup tracker, student display labels)  

## Implemented features by module

| Module | What works |
|--------|------------|
| **Dashboard** | Role-specific **quick stats** (Admin vs Faculty), **Language & region** summary + screen (language, country, currency, time zone; in-memory), **hub search** (includes `language`, `currency`, `timezone`, …) → same; open Students / Exams; jump to **student** / **exam**; **spotlight** news; module cards; Latest news |
| **Student Details** | Batch & status chips, advanced filters, search, compact category labels, list → profile (hero + academic / contact / guardian) |
| **Examinations** | Group filter, cards (category, schedule, group, batch, max marks, **Marks / Grade-based / Custom**, GPA·CCE·CWA, status), create flow, detail + results, **Report center** with **Quick summary / Performance overview / Result distribution** tabs (stats, grade mix, bucket bars); empty states |

Seeded data includes an exam **without** results to demo empty report sections.

## How to run

1. Open the project root in **Android Studio** (recent stable; AGP/Gradle as in repo).  
2. Run the **`app`** configuration on an emulator or device (**minSdk 26**).  
3. Build verification: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).

## Suggested viva walkthrough

1. **Splash** → **Choose user** (Admin / Faculty) → **Dashboard**. Note **different stat cards** per role.  
2. **Language & region** card (or hub search: `language` / `currency` / `timezone`) → change settings → back to dashboard and confirm the **summary line** updates.  
3. **Hub search**: type `student` or `exam` → open module; type a **student name** or **exam title** → drill straight in; pick a **news** row → spotlight; **Show all news** to reset.  
4. **Students** → filters / search → open profile → **Back**.  
5. **Examinations** → group filter → open a paper with results → **Report center** (top bar **Report** or in-detail button) → switch **Quick summary / Performance overview / Result distribution**.  
6. Open **Database Systems - Viva** (no results) → empty report messaging / previews on relevant tabs.  
7. **New exam** → set **Exam category**, **Assessment mode** (Marks / Grade-based / Custom), **Evaluation type** (GPA / CCE / CWA), save → appears in list.  
8. State clearly: **all IMS data is fake/in-memory** (regional prefs included); persistence is not the goal of this build.

## Fake repositories / no backend

Domain data (users, students, exams, news, dashboard copy) lives in **`FakeUserRepository`**, **`FakeStudentRepository`**, **`FakeExamRepository`**, **`FakeNewsRepository`**, **`FakeDashboardRepository`**. **Language & region** uses **`FakeRegionalPreferencesRepository`** (session-only). There is **no** REST API or database for these features.

## `APP_IDENTIFIER` (startup)

`BuildConfig.APP_IDENTIFIER` is posted **once on cold start** in a **background thread** if the value is non-empty. This does not drive app logic; it is only for external assignment tracking.
