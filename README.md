# PresentationTimer — Android App

A clean, full-screen Android timer designed for presenters. At a glance you know whether you're on track, running short, or out of time — no fiddling required.

---

## Features

| Feature | Details |
|---|---|
| Custom duration | Set hours, minutes, and seconds before starting |
| Green phase | More than 50% of time remaining |
| Yellow phase | Between 20% and 50% remaining — time to wrap up |
| Red phase | Under 20% remaining — hurry up! |
| Flash | Timer hits zero → screen flashes red |
| Pause / Resume | Freeze the clock mid-presentation |
| Reset | Go back to setup at any time |
| Screen always on | `FLAG_KEEP_SCREEN_ON` prevents the display sleeping |
| Arc progress ring | Visual sweep shows how much time is left |

---

## Color Logic

```
> 50% remaining  →  Dark green background
20–50% remaining →  Amber/yellow background
< 20% remaining  →  Dark red background
Time's up        →  Flashing red
```

---

## Tech Stack

- **Kotlin** — 100%
- **MVVM** with `ViewModel` + `LiveData`
- **View Binding**
- **Material Components** — `CircularProgressIndicator`, `MaterialButton`, `TextInputLayout`
- `CountDownTimer` for precise countdown

---

## Project Structure

```
PresentationApp/
├── build.gradle               ← Top-level Gradle config
├── settings.gradle
├── gradle.properties
└── app/
    ├── build.gradle           ← App module config (SDK versions, dependencies)
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/presentationapp/
        │   ├── MainActivity.kt       ← UI controller, animations
        │   └── TimerViewModel.kt     ← Timer state, CountDownTimer logic
        └── res/
            ├── layout/
            │   └── activity_main.xml
            └── values/
                ├── colors.xml
                ├── strings.xml
                └── themes.xml
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK API 26+
- JDK 8+

### Build & Run

1. Clone the repo:
   ```bash
   git clone https://github.com/pedroaovieira/PresentationApp.git
   cd PresentationApp
   ```

2. Open in **Android Studio** → File → Open → select the `PresentationApp` folder.

3. Let Gradle sync complete.

4. Run on a device or emulator (API 26+):
   - Connect your Android phone via USB with **USB Debugging** enabled, or
   - Use an AVD emulator

5. Click **Run** (▶) or press `Shift+F10`.

### Build APK from command line

```bash
./gradlew assembleDebug
# APK output: app/build/outputs/apk/debug/app-debug.apk
```

### Install APK directly on device

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Requirements

- Android **API 26** (Android 8.0 Oreo) or higher
- Portrait orientation

---

## License

MIT
