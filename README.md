# Rapcha

![Lifesaver](docs/assets/lifesaver-icon.png)

Rapcha is an Android floating dictation bubble. You tap the bubble, speak, and Rapcha types what you said into the text field in front of you.

![Rapcha floating dictation](docs/assets/rapcha-hero.png)

**Current version:** 27 (`lean-27`), package `com.princeniithompson.rapcha`. Not on the Play Store yet. Runs on Android 7.0+ (minSdk 24) and is tested for Android 11 (API 30) phones such as the Infinix Hot 11.

## What it does

v26 is the lean build with the v24 look brought back. There is one path:

1. Tap the floating bubble.
2. The microphone records 16 kHz mono audio.
3. The audio goes over one WebSocket to Gemini Live (`gemini-3.5-transcribe-live`). Smart Mode (Settings, on by default) cleans up filler words and punctuation; off sends the raw verbatim transcript.
4. Tap Complete. The text is typed into the focused field.

How the text is typed, in this order:

1. Accessibility `commitText` (Android 13+).
2. The Rapcha companion keyboard.
3. Accessibility `ACTION_SET_TEXT`.
4. If no text field can be found at all, the text is copied to the clipboard.

Soft speech: the app adds light, adaptive gain with a soft limiter to the audio it sends. This lets one or two quiet words be heard without shouting. The phone's own noise suppression and auto-gain are switched off, because they tend to remove quiet speech.

The bubble shows in every app, including banking apps. There is no Safe Mode.

Settings has the Smart Mode switch, the Gemini API key (typed in or fetched with Google sign-in), the Welcome Tour, Custom Vocabulary, Keep Rapcha Awake, the Floating Voice Ring switch, and the Draw Over Other Apps and Keyboard & Field Detector permissions.

Text scales with the system font size, and the screens scroll. The floating bubble keeps a fixed size so it stays on screen.

The Gemini API key is entered by the user in the app (onboarding or settings). It is stored in an on-device encrypted vault. No key is embedded in this repository or in the APK.

### Restored in v26

- The original v24 onboarding, including "How it works" and the voice test step that asks for the microphone (Android 11 and 13+). The bubble still shakes there, but there is no vibration or haptics anywhere and no VIBRATE permission.
- The v24 Home layout. The top carousel scrolls by itself when idle and can be swiped. Page 1 is the original Words bubble, page 2 is the Mini pill. Tap a bubble or "Use this bubble" to choose; the chosen one shows "Active" and is the one that floats over other apps. Swiping never changes the choice. Pages 3 and 4 are words spoken and words per minute (stored as two numbers only).
- The Home list shows this session's dictations from memory only. Nothing is written to disk, and it is empty after the app process ends.
- One glow, the v24 default "Breathing Horizon", on the live bubble. It follows your voice and pauses when the bubble is hidden. There is no glow picker and no customization page.
- The "apps you're flowing in" stat and the Lite badge are gone.

### Removed in v25 (still removed unless listed above)

- The separate Polish stack (Lyra and three-step). Smart Mode is back as a Settings switch in v26.
- App detection
- Safe Mode
- Lite mode and the render budget
- Extra recording glows (one default glow is back in v26)
- Retry recordings
- Saved history and recordings (v26 keeps only an in-memory list)
- Vocabulary learning
- Haptics and vibration
- Release logging (logs are stripped by R8)
- Firebase and Retrofit

## Repository layout

```
app/                  Android app module
  src/main/
    AndroidManifest.xml
    java/com/princeniithompson/rapcha/
      RapchaApplication.kt   Process start, legacy data cleanup
      MainActivity.kt        Hosts the in-app screens
      audio/                 AudioRecorder (mic thread) and SpeechGain (soft-speech gain + limiter)
      core/                  API key vault and Gemini endpoints
      data/                  Custom vocabulary, onboarding state, legacy cleanup
      diagnostics/           Bubble health counters
      service/               Bubble service, accessibility service, companion keyboard, recovery
        floating/            Dictation session, ordered audio sender, text injection, overlay
      ui/                    Jetpack Compose screens and bubble UI
        screens/onboarding/  First-run permissions and Gemini key setup
      websocket/             Gemini Live WebSocket client and transcript handling
    res/                     Drawables, strings, font, accessibility and IME XML
    baseline-prof.txt        Baseline profile for release startup
  src/test/                  Unit tests and font-scale screenshots (no device required)
  build.gradle.kts           App version, SDK levels, signing hooks, dependencies
  proguard-rules.pro         R8 rules for release (strips logging)
docs/                        Engineering notes and README images
gradle/                      Version catalog and Gradle wrapper
build.gradle.kts             Root plugins
settings.gradle.kts          Project name and module list
gradle.properties            Gradle JVM and Android flags
.env.example                 Placeholder only (GEMINI_API_KEY=MY_GEMINI_API_KEY)
metadata.json                Google AI Studio project metadata
```

Useful files if you are changing behaviour:

- Typing into other apps (critical, keep the order): `service/floating/FloatingTextInjector.kt`, `service/floating/RapchaTextInserter.kt`, `service/RapchaAccessibilityService.kt`, `service/RapchaInputMethodService.kt`
- Microphone and soft speech: `audio/AudioRecorder.kt`, `audio/SpeechGain.kt`, `websocket/TranscriptAcceptance.kt`
- Live transcription socket and system prompt: `websocket/GeminiLiveWebSocketClient.kt`
- Session flow (start, Complete, cancel): `service/floating/FloatingDictationSessionManager.kt`, `service/FloatingBubbleService.kt`
- API key: `core/ApiKeyVault.kt`, `core/ApiConfig.kt`

## How to build

You need:

- **JDK 17 or newer.** This tree was verified with Temurin 21. The Android compile options target Java 11. Kotlin is 2.2.10; the Android Gradle Plugin is 9.1.1.
- **Android SDK** with platform **36** (and 36.1 if your SDK manager offers it). Set the path in a local `local.properties` file (this file is gitignored):

  ```
  sdk.dir=/path/to/Android/Sdk
  ```

  Android Studio writes this for you.

Debug build (unsigned except for the usual local debug keystore):

```bash
./gradlew assembleDebug
```

The debug APK lands at `app/build/outputs/apk/debug/`. Do not commit APKs.

A local `debug.keystore` at the repo root is used for debug signing, with the standard Android debug alias and password already in `app/build.gradle.kts`. If the file is missing, generate one with `keytool` (and leave it uncommitted).

### Release signing

Release builds are minified with R8 and resource shrinking. Signing is **not** in this repo. The build reads these environment variables and will refuse to assemble a release without them:

- `KEYSTORE_PATH` — path to your `.jks` / `.keystore` file
- `STORE_PASSWORD`
- `KEY_PASSWORD`
- `KEY_ALIAS` — optional, defaults to `upload` (the Rapcha release key uses `rapcha`)

Keep the keystore, `keystore.properties`, `store.pass`, and `.env` on your machine only. They are listed in `.gitignore`.

## Gemini API key

The app never ships with a real key.

- The user pastes their own Gemini key during onboarding or in settings.
- `ApiKeyVault` encrypts it with the Android Keystore and stores it on the device.
- `ApiConfig.resolveApiKey()` uses that vault (or a manual setting). It does not fall back to `BuildConfig`.
- The Secrets Gradle plugin is configured to ignore `GEMINI_API_KEY`, so a local `.env` cannot leak a key into `BuildConfig`.
- `.env.example` only contains the placeholder `MY_GEMINI_API_KEY`.

Tests may use obvious fakes, or `AQ.FakeUnitTestKeyNotReal000000000000000000000`. Those are not real keys.

## Notes for AI agents (Google AI Studio and others)

- This repository is the clean lean tree (v25 onward). Do not pull history or files from the older `princeniithompson/Vs` repo unless the owner asks.
- Do not commit secrets, keystores, `local.properties`, `.env`, APKs, or AI Studio screenshot dumps.
- **Do not change text injection behaviour.** Keep the order: P1 `commitText` (API 33+), P2 companion keyboard, P3 `ACTION_SET_TEXT`, then the clipboard only when there is no target field. `InjectionChainVerificationTest` guards this.
- Keep the single path: bubble, mic, one Live WebSocket, inject. Do not bring back polish, app detection, Safe Mode, or history without the owner asking.
- Do not hardcode heights for text. Use `heightIn` and scrolling so large system fonts wrap.
- Prefer small, focused diffs. Run `./gradlew test` and `./gradlew assembleDebug` after behaviour changes when you can.
- The application id and namespace are `com.princeniithompson.rapcha`.

## License

TBD.
