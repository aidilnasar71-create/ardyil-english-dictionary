#!/usr/bin/env bash
set -euo pipefail

ROOT="${1:-.}"
HTML="$ROOT/index.html"
NATIVE="$ROOT/native-android/MainActivity.java"
MANIFEST="$ROOT/AndroidManifest.xml"

fail() {
  echo "::error::ARDYIL audio core regression: $1" >&2
  exit 1
}

require_text() {
  local file="$1"
  local expected="$2"
  [[ -f "$file" ]] || fail "File inti tidak ditemukan: $file"
  grep -Fq -- "$expected" "$file" || fail "Komponen wajib hilang dari $file: $expected"
}

# Native bridge registration and permission gate.
require_text "$NATIVE" 'webView.addJavascriptInterface(new ArdyilNativeBridge(), "ArdyilNative");'
require_text "$NATIVE" 'public final class ArdyilNativeBridge'
require_text "$NATIVE" '@JavascriptInterface'
require_text "$NATIVE" 'public void testMic('
require_text "$NATIVE" 'public void startRecording('
require_text "$NATIVE" 'public void stopRecording('
require_text "$NATIVE" 'public void speak('
require_text "$NATIVE" 'public void stopSpeech('
require_text "$NATIVE" 'REQUEST_NATIVE_AUDIO'
require_text "$NATIVE" 'Manifest.permission.RECORD_AUDIO'

# Recording format, lifecycle, and playback endpoint.
require_text "$NATIVE" 'MediaRecorder.AudioSource.MIC'
require_text "$NATIVE" 'MediaRecorder.OutputFormat.MPEG_4'
require_text "$NATIVE" 'MediaRecorder.AudioEncoder.AAC'
require_text "$NATIVE" 'private void startNativeRecording()'
require_text "$NATIVE" 'private void stopNativeRecording()'
require_text "$NATIVE" 'private WebResourceResponse serveRecording('
require_text "$NATIVE" 'RECORDING_URL'

# Android Text-to-Speech engine must remain available.
require_text "$NATIVE" 'new TextToSpeech(this'
require_text "$NATIVE" 'private void speakPendingText()'
require_text "$NATIVE" 'TextToSpeech.QUEUE_FLUSH'
require_text "$NATIVE" 'TextToSpeech.QUEUE_ADD'

# Web app must keep calling the native bridge and handling its callbacks.
require_text "$HTML" "window.ArdyilNative.speak("
require_text "$HTML" "window.ArdyilNative.testMic()"
require_text "$HTML" "window.ArdyilNative.startRecording()"
require_text "$HTML" "window.ArdyilNative.stopRecording()"
require_text "$HTML" "window.ardyilNativeMicReady=function"
require_text "$HTML" "window.ardyilNativeRecordStarted=function"
require_text "$HTML" "window.ardyilNativeRecordComplete=function"
require_text "$HTML" "window.ardyilNativeRecordError=function"
require_text "$HTML" "window.ardyilNativeSpeechError=function"
require_text "$HTML" 'id="recordPlayer"'
require_text "$HTML" 'id="startRecord"'
require_text "$HTML" 'id="stopRecord"'

# Android runtime microphone permission is part of the app contract.
require_text "$MANIFEST" 'android.permission.RECORD_AUDIO'

echo "ARDYIL audio core OK: native recorder, playback, microphone permission, and Android TTS are present."
