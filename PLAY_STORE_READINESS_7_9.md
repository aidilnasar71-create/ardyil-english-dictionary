# ARDYIL 7.9 — Play Store readiness checklist

This is a development/debug build, not yet a Play Store release.

## Required before publishing
- [ ] Configure Google OAuth / Firebase Authentication securely or remove account UI; never represent a placeholder as real login.
- [ ] Add cloud sync only with security rules and verified authentication.
- [ ] If accounts are enabled, provide in-app account deletion and a public account deletion URL.
- [ ] Publish an accurate privacy policy and complete Data safety disclosures.
- [ ] Confirm target age groups. SD content means a mixed/child audience review is required; evaluate Families policies, age screening, parental/privacy safeguards and sharing controls.
- [ ] Ensure all books, comic art, illustrations, video, music and sound assets are original, public domain or properly licensed.
- [ ] Test microphone, recording playback, TTS, Android share chooser, offline mode and local data on real devices.
- [ ] Complete accessibility, crash, performance, security and content review.
- [ ] Create and securely store a release signing key; never commit it.
- [ ] Build a signed release Android App Bundle (AAB), not a debug APK.
- [ ] Finish Play listing, screenshots, content rating, privacy and policy declarations.

## Build target
Build workflow targets API 36 and keeps Android 13 compatibility at the minimum SDK. Target API does not mean Android 16-only.

## Current 7.9 content (implemented in source)
- Five learning tracks (SD, SMP, SMA, university, adult), eight unit topics each (40 units), with offline vocabulary cards and mini quizzes.
- Eight comic series with eight episodes each (64 episode entries); four bilingual text panels each (256 panel entries). The panels currently use emoji/illustrative cards and dialogue, not commissioned Webtoon-grade drawn art.
- Existing story studio, dictionary, puzzle, daily mission, adventure, math-in-English, novel reader, audio/microphone and settings remain.
- Android native Share chooser for sharing text invitations, stories, comic dialogue and progress; destinations depend on installed apps.
- Local preferences and learning progress.

## Not activated
- Google sign-in / cloud sync: requires owner OAuth client and Firebase project configuration, secure implementation, account deletion, age-appropriate review and privacy disclosures.
- Full illustrated comics and larger downloadable video/audio media packs are roadmap content.
