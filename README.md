# Institute Management System

A native Android Institute Management System built with **Kotlin** and **Jetpack Compose**.

The app includes role-aware dashboards, student management, examination workflows, and performance analytics.

## Features

### Dashboard

- Admin and Faculty roles
- Institute statistics
- Search across students, exams, and news
- Module shortcuts
- Latest announcements

### Student Management

- Search by name or roll number
- Filter by batch and status
- Advanced filters
- Student profile pages
- Overview, examinations, and notes sections
- Empty-state handling

### Examinations

- Browse and filter examinations
- Create new examinations
- View exam details and results
- Handle exams without published results
- View average, highest, lowest, and pass percentage
- Visualize score distribution

## Screenshots

| Dashboard | Student Management |
|---|---|
| ![Admin Dashboard](screenshots/Admin%20Dashboard%20-%20Full%20Page.png) | ![Students List](screenshots/Students%20List%20-%20Full%20Page.png) |

| Examinations | Analytics |
|---|---|
| ![Examinations](screenshots/Examinations%20List.png) | ![Report Center](screenshots/Report%20Center%20-%20Full%20Page.png) |

| Student Profile | Exam Results |
|---|---|
| ![Student Profile](screenshots/Student%20Profile%20-%20Leo%20Overview.png) | ![Exam Results](screenshots/Exam%20Detail%20-%20With%20Results.png) |

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel
- StateFlow
- Gradle

## Architecture

The app follows a feature-oriented structure with a clear separation between UI, state, and data.

- `MainActivity` hosts the app theme and navigation graph.
- `ViewModel` and `StateFlow` manage UI state.
- Repository interfaces separate the UI from the data source.
- In-memory repositories provide local demo data.
- Navigation Compose connects dashboard, student, examination, and report flows.

## Data Layer

The app uses repository interfaces with in-memory implementations to simulate persistence without requiring a backend.

The current data layer can later be replaced with Room, Firebase, Supabase, or a REST API without changing the UI structure.

## Project Structure

```text
.
├── app/
│   └── src/main/
│       ├── java/com/institute/ims/
│       │   ├── data/
│       │   ├── ui/
│       │   ├── utils/
│       │   └── MainActivity.kt
│       └── res/
├── gradle/
├── screenshots/
├── build.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

## Getting Started

### Requirements

- Android Studio
- JDK 17
- Android SDK
- Android emulator or physical device
- Minimum SDK 26

### Clone

```bash
git clone https://github.com/anushkaps/institute-management-system.git
cd institute-management-system
```

### Run

Open the project in Android Studio and run the `app` configuration.

Or build from the terminal.

macOS or Linux:

```bash
chmod +x gradlew
./gradlew assembleDebug
```

Windows:

```bash
gradlew.bat assembleDebug
```

## Design

The interface uses consistent layouts, visible feedback, and reduced memory load.

- Blue represents dashboard and news
- Green represents student management
- Purple represents examinations
- Amber represents reports and analytics

Low-fidelity wireframes and high-fidelity mockups were created in Figma before implementation.

## Contributors

- [Anushka Pratap Singh](https://github.com/anushkaps)
- [Singam Sai Asritha](https://github.com/Asritha-Singam)

## Project Context

Originally developed as a collaborative software design project at IIIT Hyderabad.
