# ARDYIL 7.9 — Play Store readiness checklist

This repository build is a development/debug build, not yet a Play Store release.

## Required before submission
- [ ] Finalize Google OAuth/Firebase authentication, or remove sign-in UI and ship without accounts.
- [ ] If accounts are enabled, provide in-app account deletion and a public external deletion URL; delete associated account data.
- [ ] Publish a globally accessible, accurate privacy policy and complete the Play Console Data safety form.
- [ ] Confirm the declared age groups. Elementary-school users make this a mixed-audience/child-safety project; review Families policy, neutral age screening, APIs/SDKs, sharing controls and parent controls.
- [ ] Verify all book, comic, illustration, audio, music and video assets are original, public domain, or properly licensed.
- [ ] Test microphone, recording playback, TTS, share chooser, offline mode and data reset on physical Android devices.
- [ ] Complete accessibility, crash, performance, security and content review.
- [ ] Create and safely store a release signing key; never commit it.
- [ ] Build a signed Android App Bundle (AAB) with the release key, not a debug APK.
- [ ] Complete store listing, screenshots, content rating, app access instructions, privacy policy URL and declarations.

## Build compatibility
New Google Play apps and app updates must target Android 16 (API 36) or higher from 31 August 2026. The pipeline therefore attempts to build the 7.9 package with API 36. Android 13 devices remain supported by the project's minimum SDK; target API level does not mean Android 16-only.

## Current 7.9 content
- Five curriculum tracks: SD, SMP, SMA, university and professional/adult.
- Eight learning units per track, each with four offline vocabulary prompts and short quizzes.
- Six original comic series with five episode titles each, vertical scroll panels and Indonesian translations.
- Six illustrated animated story experiences with 3- or 5-minute playback timelines, scene motion, bilingual captions and TTS narration.
- Local XP/progress storage and Android native share chooser.
- Existing dictionary, puzzle, daily challenge, adventure, novel, media, settings and protected audio/microphone modules remain in the app.
