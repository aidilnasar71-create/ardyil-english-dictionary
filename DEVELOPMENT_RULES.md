# ARDYIL development rules — protected audio core

## Non-negotiable rule

**Do not remove, replace with a browser-only implementation, rename without updating every caller, or silently disable the audio and microphone features.** They are core ARDYIL capabilities and must remain available in every future APK unless the project owner explicitly asks to remove them.

## Canonical audio implementation

- `native-android/MainActivity.java` is the source of truth for the native Android bridge. The APK workflow copies this file into the extracted Android project's `MainActivity.java`.
- `index.html` calls the bridge through `window.ArdyilNative`.
- `AndroidManifest.xml` must retain `android.permission.RECORD_AUDIO`.
- The native bridge uses Android `MediaRecorder` with MPEG-4/AAC, serves the saved recording to the WebView player, and uses Android `TextToSpeech` for spoken examples.

## Required bridge contract

The following JavaScript methods must keep working:

- `ArdyilNative.testMic()`
- `ArdyilNative.startRecording()`
- `ArdyilNative.stopRecording()`
- `ArdyilNative.speak(text, language, rate, repeats)`
- `ArdyilNative.stopSpeech()`

The WebView callbacks for microphone readiness, recording start/completion/errors, and speech errors must also remain connected to the page.

## Rules for future feature work

1. Keep the native audio bridge and recording playback endpoint intact when editing `MainActivity.java`.
2. Keep the recording controls and native-bridge calls intact when editing `index.html`.
3. Do not remove the `RECORD_AUDIO` permission from the manifest.
4. Run `bash scripts/check-audio-core.sh .` before building. GitHub Actions runs this guard automatically and the build must fail if a required audio component is missing.
5. After any change to the audio code, test on a real Android phone: grant microphone permission, record for at least 3 seconds, stop, play the saved recording, play an English TTS example, and stop speech.
6. Do not claim audio has been device-tested unless that test was actually performed on a physical device.

## Device and data safeguards

- Keep the app compatible with Android 13 and the existing Android WebView asset origin.
- Keep recordings in app-private storage unless a separate export feature is deliberately implemented.
- Do not introduce camera access or unrelated permissions as part of audio maintenance.
- A missing English TTS voice package must produce a clear user message; it must not break recording.

**Any future UI redesign, game, novel, music, dictionary, or settings update must preserve this audio core.**
