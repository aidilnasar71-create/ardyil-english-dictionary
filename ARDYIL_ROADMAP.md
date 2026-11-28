# ARDYIL Roadmap (4.2 and beyond)

## Core safety rule
Audio pronunciation and native microphone recording are protected core features. Every release must run `bash scripts/check-audio-core.sh .`; never remove Android MediaRecorder, TextToSpeech, playback, callbacks, or the RECORD_AUDIO permission.

## 4.2 current polish
- Animated splash and rotating motivational greetings.
- Extended offline synthesizer instrumental preset menu (25+ sound profiles; synthetic tones, not licensed recorded tracks).
- Existing learning navigation, puzzles, daily challenges, adventure path, math-in-English, novel reader, animated story, vertical comic reader, starter multilingual phrase library, and settings remain in place.

## Planned expansions
- Expand vocabulary, phrase, slang and idiom packs; smart free-form sentence search/translation.
- Daily and weekly mission variety, speaking recording quests, listening quizzes and sound feedback.
- Larger bilingual novel catalog with chapters, saved progress, font/theme controls, and more mood-based instrumental presets.
- Comic episodes with translation controls and original/licensed panels.
- Short educational animation/story library, 3–5 minutes; downloadable optional media packs.
- More language starter content and translation languages.
- Broad accessibility/preferences: themes, accent colors, font size, animation reduction, sound/music volume, reminders, battery/data/storage controls.

## Media and licensing
Use original, public-domain or properly licensed text, illustrations, audio, comics and videos. Large media should be optional downloads to avoid unnecessarily inflating base APK.

## Compatibility and QA
Target Android 13 devices including Infinix Smart 8. Do not claim physical-device tests unless performed. Test audio speak/stop, record >=3 sec, stop and playback on physical Android after every audio-affecting release.
