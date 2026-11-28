# Google Sign-In setup for ARDYIL 7.9

Google login is deliberately shown as unavailable until the app owner completes configuration. No fake login is used.

## Owner setup
1. Create a Google Cloud project; configure Google Auth Platform consent screen and app branding.
2. Register Android package \`com.ardyil.dictionary\`.
3. Add SHA-1 and SHA-256 debug and Play App Signing fingerprints.
4. Create a Web OAuth client ID for the Android Credential Manager flow.
5. Configure Firebase Authentication (Google provider) and secure cloud database rules if syncing progress.
6. Provide configuration through the secure setup workflow; do not commit service-account keys, keystore files or passwords.

## Implementation/security
Use Android Credential Manager Sign in with Google; verify ID tokens via Firebase/backend. Do not trust JavaScript-supplied email/name as identity proof. Provide sign-out, account deletion and data deletion workflow before accounts go live.

## Mixed-audience and children
Because ARDYIL includes SD learners, login and cloud sync must be reviewed for child/mixed audience policy. Keep learning available without account, apply age-appropriate neutral onboarding, minimize collection, protect child progress and review sharing features and Play Families declarations.

## Current status
- Offline progress and preferences: local on device.
- Android share chooser: native bridge implemented; app-target availability depends on installed apps.
- Google sign-in and cloud sync: not active. Owner OAuth/Firebase configuration is required.
