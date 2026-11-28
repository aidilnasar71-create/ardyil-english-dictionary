# Google Sign-In and cloud sync setup for ARDYIL 7.9

Google login is deliberately not marked active in the 7.9 APK. The UI explains this state instead of faking a successful sign-in.

## Required owner configuration

1. Create a Google Cloud project and configure the Google Auth Platform consent screen and app branding.
2. Register the Android application ID: `com.ardyil.dictionary`.
3. Add SHA-1 and SHA-256 fingerprints for the debug certificate and the final Play App Signing certificate.
4. Create a Web application OAuth client ID. The Android Credential Manager Google ID request uses this Web client ID as its server client ID.
5. If cloud progress sync is required, configure Firebase Authentication with Google as a provider and a database (for example Cloud Firestore) with per-user security rules.
6. Complete Google brand verification where required. Do not put private service-account keys, signing keystores, passwords, or Firebase Admin credentials in this repository or in the APK.
7. Share the Web OAuth client ID and the Firebase project configuration through the secure project setup process so the native Credential Manager + Firebase Auth implementation can be completed.

## Android integration requirements

Use the current Android Credential Manager Sign in with Google flow, not the legacy Google Sign-In API and not a password form inside the WebView. The Android app must verify the ID token through the configured auth provider/backend before treating the user as signed in. Never trust a display name or email passed from JavaScript as proof of identity.

## Child and mixed-audience safety

ARDYIL contains learning tracks for elementary school through adult learners. Before enabling Google login or cloud sync for all tracks, perform an age-appropriate neutral audience flow and verify the authentication SDK/service is permitted for the declared audience. Keep child-mode learning available without requiring an account; limit sharing and collection of personal data in child mode. Complete the applicable Google Play Families declarations and legal review before distribution.

## Account deletion

When account creation is enabled, add an in-app account deletion workflow and a public web account-deletion request page. The workflow must delete the authentication account and associated cloud data, with any legally required retention disclosed clearly. A sign-out button alone is not account deletion.

## Current status

- Local learning progress: stored on the device.
- Android share chooser: implemented through the native bridge.
- Google sign-in: **not active** until the project owner supplies the OAuth/Firebase configuration and the secure authentication flow is integrated.
- Cloud sync: **not active** until the backend and security rules are configured.
