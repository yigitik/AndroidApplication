# Conference Registration & Verification System

An Android application developed for managing conference participant registration and verification. The project follows the **MVVM architecture** and uses **Room Database** for local data persistence while leveraging modern Android Jetpack components.

---

## Features

### Participant Registration
- Register conference participants with a unique ID
- Enter participant information:
  - Full Name
  - Academic Title
  - Registration Type
- Capture a profile photo using the device camera
- Securely store images using **FileProvider**
- Validate user input before saving
- Prevent duplicate participant IDs

### Participant Verification
- Search participants by User ID
- Display participant information if found
- Load profile images using **Glide**
- Change the interface color according to the registration type:
  - 🟢 Full Registration
  - 🔵 Student Registration
  - 🟠 No Registration
- Display an error screen when the participant does not exist

---

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM
- **Database:** Room (SQLite)
- **UI:** XML + ViewBinding
- **Image Loading:** Glide
- **Camera:** Activity Result API + FileProvider
- **Minimum SDK:** 24
- **Target SDK:** 36

---

## Project Architecture

```
UI
│
├── ViewModels
│
├── Repository
│
├── Room Database
│
└── Local Storage
```

### Data Layer
- Room Entity
- DAO
- Room Database

### Repository Layer
- Handles all database operations
- Serves as the single source of truth for data access

### ViewModel Layer
- Separates business logic from the UI
- Uses LiveData for reactive state management

### UI Layer
- Main Activity
- Registration Screen
- Verification Screen

---

## Database Schema

```kotlin
Participant(
    userId: Int,
    fullName: String,
    title: String,
    registrationType: Int,
    photoPath: String?
)
```

---

## Validation

The application performs several validation checks:

- Unique participant ID
- Required field validation
- Camera permission handling
- Graceful error handling without application crashes

---

## Libraries

- Room Database
- Android ViewModel
- LiveData
- Glide
- Activity Result API
- FileProvider

---

## Workflow

### Registration

1. Enter participant information.
2. Capture a profile photo.
3. Validate all fields.
4. Save the participant into the Room database.

### Verification

1. Enter a participant ID.
2. Search the local database.
3. Display participant information if found.
4. Update the UI according to the registration type.

---

## Error Handling

- Duplicate participant IDs
- Invalid or empty inputs
- Camera permission denial
- Missing participant records
- Image capture failures

All errors are handled gracefully without crashing the application.

---

## Project Highlights

- MVVM Architecture
- Repository Pattern
- Room Database
- ViewBinding
- Modern Camera API
- Local Image Storage
- Reactive UI with LiveData
- Clean and maintainable project structure
