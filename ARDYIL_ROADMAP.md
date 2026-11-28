# ARDYIL 7.9 Roadmap & protected features

## Release 7.9 additions
- Eight age/learning-oriented comic series: SD, SMP, SMA, university, adult/work, mystery, friendship, science/future.
- Eight episode slots per series (64 episode screens), four bilingual dialogue panels per episode (256 panel entries); text is currently template-generated, not finished illustrated Webtoon art.
- Learning Campus with five learner tracks and eight unit topics each (40 unit buttons); vocabulary cards and mini-quiz are offline starter practice.
- Android native Share chooser for story, comic, progress, and app invitation text. Actual destination platforms depend on apps installed on the device.
- Local preferences for font size, reduced motion, effects toggle and learner track.
- Google login is shown as not configured; never fake authentication. OAuth/Firebase and child/mixed-audience policy setup are required before activating it.

## Protected core
Audio pronunciation and native microphone recording are protected features. Every release must run \`bash scripts/check-audio-core.sh .\`; never remove Android MediaRecorder, TextToSpeech, playback, callbacks, or the RECORD_AUDIO permission.

## Existing learning modules retained
- Dictionary, offline search, quiz, favorite words, sentence patterns.
- Speaking, pronunciation and native Android mic recorder.
- Puzzles, daily challenges, adventure path, math-in-English.
- Novel reader, mood instrument presets, story studio, language phrase library.
- Settings, themes and local progress.

## Content and media roadmap
- Expand vocab, idioms, slang, phrases, and free-form sentence search.
- More mission sets, speaking/listening quizzes and review of mistakes.
- Bilingual novels with chapter packs and optional instrumental audio.
- Original/licensed Webtoon-like comic artwork, long-form episodes, and optional download packs.
- Short visual animation packs with captioning, narration and offline controls.
- Further language packs and accessibility settings.

## Licensing and size strategy
Use original, public-domain or properly licensed text, art, audio and videos. Keep the base app lightweight; large story/comic/music packs should be optional downloads. A large APK by itself does not make the product richer; install size, memory, storage, battery, accessibility and performance matter.

## Android compatibility & QA
Target Android 13 devices including Infinix Smart 8; Android target SDK does not mean older phones become unsupported. Do not claim physical device tests unless performed. After each release, test TTS speak/stop, record at least three seconds, stop/playback, sharing chooser, offline modules and saved preferences on physical Android.
