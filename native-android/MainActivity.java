package com.ardyil.dictionary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

/**
 * Native Android shell for ARDYIL. Audio recording and speech use Android APIs
 * so they do not depend on WebView getUserMedia or browser speech synthesis.
 */
public class MainActivity extends Activity {
    private static final int REQUEST_WEB_AUDIO = 7314;
    private static final int REQUEST_NATIVE_AUDIO = 7315;
    private static final String APP_ASSET_HOST = "appassets.androidplatform.net";
    private static final String START_URL = "https://" + APP_ASSET_HOST + "/assets/index.html";
    private static final String RECORDING_URL =
            "https://" + APP_ASSET_HOST + "/recordings/latest.m4a";

    private WebView webView;
    private PermissionRequest pendingAudioRequest;
    private WebViewAssetLoader assetLoader;

    private MediaRecorder mediaRecorder;
    private File recordingFile;
    private boolean nativeRecording = false;
    private boolean pendingNativeStart = false;
    private boolean pendingNativeMicTest = false;

    private TextToSpeech textToSpeech;
    private boolean ttsInitializing = false;
    private boolean ttsReady = false;
    private String pendingSpeechText = "";
    private String pendingSpeechLocale = "en-US";
    private float pendingSpeechRate = 0.85f;
    private int pendingSpeechRepeats = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureSystemBars();

        assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(247, 246, 251));
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }

        webView.addJavascriptInterface(new ArdyilNativeBridge(), "ArdyilNative");

        webView.setWebViewClient(new WebViewClientCompat() {
            @Override
            public WebResourceResponse shouldInterceptRequest(
                    WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && APP_ASSET_HOST.equalsIgnoreCase(uri.getHost())
                        && "/recordings/latest.m4a".equals(uri.getPath())) {
                    return serveRecording(request);
                }
                return assetLoader.shouldInterceptRequest(uri);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                return uri == null || !"https".equalsIgnoreCase(uri.getScheme())
                        || !APP_ASSET_HOST.equalsIgnoreCase(uri.getHost());
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> handleWebPermissionRequest(request));
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                if (pendingAudioRequest == request) {
                    pendingAudioRequest = null;
                }
            }
        });

        FrameLayout root = new FrameLayout(this);
        root.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(root);

        if (savedInstanceState == null) {
            webView.loadUrl(START_URL);
        } else if (webView.restoreState(savedInstanceState) == null) {
            webView.loadUrl(START_URL);
        }
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(23, 21, 43));
        window.setNavigationBarColor(Color.rgb(23, 21, 43));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);
            window.setStatusBarContrastEnforced(false);
        }
    }

    private void handleWebPermissionRequest(PermissionRequest request) {
        if (isFinishing() || isDestroyed()) {
            request.deny();
            return;
        }
        Uri origin = request.getOrigin();
        if (origin == null || !"https".equalsIgnoreCase(origin.getScheme())
                || !APP_ASSET_HOST.equalsIgnoreCase(origin.getHost())) {
            request.deny();
            return;
        }
        boolean audioRequested = false;
        for (String resource : request.getResources()) {
            if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(resource)) {
                audioRequested = true;
            } else {
                request.deny();
                return;
            }
        }
        if (!audioRequested) {
            request.deny();
            return;
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
            return;
        }
        if (pendingAudioRequest != null) pendingAudioRequest.deny();
        pendingAudioRequest = request;
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_WEB_AUDIO);
    }

    private void requestNativeMic(boolean shouldStart) {
        pendingNativeStart = shouldStart;
        pendingNativeMicTest = !shouldStart;
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            pendingNativeStart = false;
            pendingNativeMicTest = false;
            if (shouldStart) {
                startNativeRecording();
            } else {
                dispatchJs("ardyilNativeMicReady", "");
            }
            return;
        }
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_NATIVE_AUDIO);
    }

    private void startNativeRecording() {
        if (nativeRecording) {
            dispatchJs("ardyilNativeRecordError", "Perekaman sedang berjalan.");
            return;
        }
        try {
            File directory = new File(getFilesDir(), "recordings");
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IllegalStateException("Folder penyimpanan rekaman tidak dapat dibuat.");
            }
            recordingFile = new File(directory, "latest.m4a");
            if (recordingFile.exists() && !recordingFile.delete()) {
                throw new IllegalStateException("Rekaman lama tidak dapat diganti.");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mediaRecorder = new MediaRecorder(this);
            } else {
                mediaRecorder = new MediaRecorder();
            }
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setOutputFile(recordingFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            nativeRecording = true;
            dispatchJs("ardyilNativeRecordStarted", "");
        } catch (Exception e) {
            releaseRecorder();
            dispatchJs("ardyilNativeRecordError",
                    "Gagal memulai rekaman: " + safeMessage(e));
        }
    }

    private void stopNativeRecording() {
        if (mediaRecorder == null || !nativeRecording) {
            dispatchJs("ardyilNativeRecordError", "Belum ada rekaman yang sedang berjalan.");
            return;
        }
        try {
            mediaRecorder.stop();
            nativeRecording = false;
            releaseRecorder();
            if (recordingFile == null || !recordingFile.exists() || recordingFile.length() < 700) {
                throw new IllegalStateException("File rekaman kosong. Coba bicara lebih lama.");
            }
            dispatchJs("ardyilNativeRecordComplete", RECORDING_URL);
        } catch (RuntimeException e) {
            nativeRecording = false;
            releaseRecorder();
            if (recordingFile != null) recordingFile.delete();
            dispatchJs("ardyilNativeRecordError",
                    "Rekaman terlalu singkat atau gagal disimpan. Coba rekam beberapa detik lagi.");
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            try { mediaRecorder.reset(); } catch (Exception ignored) {}
            try { mediaRecorder.release(); } catch (Exception ignored) {}
            mediaRecorder = null;
        }
        nativeRecording = false;
    }

    private WebResourceResponse serveRecording(WebResourceRequest request) {
        if (recordingFile == null || !recordingFile.exists()) {
            return new WebResourceResponse("text/plain", "UTF-8", 404, "Not Found",
                    Collections.emptyMap(),
                    new ByteArrayInputStream("Recording not found".getBytes()));
        }
        try {
            long total = recordingFile.length();
            long start = 0;
            long end = total - 1;
            boolean partial = false;
            String range = null;
            if (request.getRequestHeaders() != null) {
                for (Map.Entry<String, String> header : request.getRequestHeaders().entrySet()) {
                    if ("Range".equalsIgnoreCase(header.getKey())) {
                        range = header.getValue();
                        break;
                    }
                }
            }
            if (range != null && range.startsWith("bytes=")) {
                String[] parts = range.substring(6).split("-", 2);
                start = Long.parseLong(parts[0]);
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    end = Math.min(end, Long.parseLong(parts[1]));
                }
                if (start < 0 || start > end || start >= total) {
                    return new WebResourceResponse("audio/mp4", "UTF-8", 416,
                            "Range Not Satisfiable", Collections.emptyMap(),
                            new ByteArrayInputStream(new byte[0]));
                }
                partial = true;
            }
            FileInputStream input = new FileInputStream(recordingFile);
            long skipped = 0;
            while (skipped < start) {
                long amount = input.skip(start - skipped);
                if (amount <= 0) break;
                skipped += amount;
            }
            long length = end - start + 1;
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept-Ranges", "bytes");
            headers.put("Content-Length", String.valueOf(length));
            headers.put("Cache-Control", "no-store");
            if (partial) headers.put("Content-Range",
                    "bytes " + start + "-" + end + "/" + total);
            return new WebResourceResponse("audio/mp4", "UTF-8",
                    partial ? 206 : 200, partial ? "Partial Content" : "OK",
                    headers, new LimitedInputStream(input, length));
        } catch (Exception e) {
            return new WebResourceResponse("text/plain", "UTF-8", 500, "Read Error",
                    Collections.emptyMap(),
                    new ByteArrayInputStream(safeMessage(e).getBytes()));
        }
    }

    private static final class LimitedInputStream extends java.io.FilterInputStream {
        private long remaining;
        LimitedInputStream(InputStream input, long length) {
            super(input);
            remaining = length;
        }
        @Override public int read() throws java.io.IOException {
            if (remaining <= 0) return -1;
            int value = super.read();
            if (value >= 0) remaining--;
            return value;
        }
        @Override public int read(byte[] buffer, int offset, int length)
                throws java.io.IOException {
            if (remaining <= 0) return -1;
            int count = super.read(buffer, offset, (int) Math.min(length, remaining));
            if (count > 0) remaining -= count;
            return count;
        }
    }

    private void speakNative(String text, String language, float rate, int repeats) {
        pendingSpeechText = text == null ? "" : text.trim();
        pendingSpeechLocale = (language == null || language.trim().isEmpty()) ? "en-US" : language;
        pendingSpeechRate = Math.max(0.5f, Math.min(1.5f, rate));
        pendingSpeechRepeats = Math.max(1, Math.min(3, repeats));
        if (pendingSpeechText.isEmpty()) return;

        if (ttsReady && textToSpeech != null) {
            speakPendingText();
            return;
        }
        if (ttsInitializing) return;

        ttsInitializing = true;
        textToSpeech = new TextToSpeech(this, status -> {
            ttsInitializing = false;
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                ttsReady = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
                    textToSpeech.setAudioAttributes(attributes);
                }
                speakPendingText();
            } else {
                dispatchJs("ardyilNativeSpeechError",
                        "Mesin Text-to-Speech Android tidak tersedia. Pasang atau aktifkan layanan Text-to-Speech di Setelan HP.");
            }
        });
    }

    private void speakPendingText() {
        if (!ttsReady || textToSpeech == null) return;
        Locale locale = Locale.forLanguageTag(pendingSpeechLocale);
        if (textToSpeech.isLanguageAvailable(locale) < TextToSpeech.LANG_AVAILABLE) {
            locale = Locale.US;
        }
        int languageResult = textToSpeech.setLanguage(locale);
        if (languageResult == TextToSpeech.LANG_MISSING_DATA
                || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            dispatchJs("ardyilNativeSpeechError",
                    "Paket suara English belum tersedia. Pasang suara English di pengaturan Text-to-Speech Android.");
            return;
        }
        textToSpeech.setSpeechRate(pendingSpeechRate);
        for (int i = 0; i < pendingSpeechRepeats; i++) {
            textToSpeech.speak(pendingSpeechText,
                    i == 0 ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD,
                    null, "ARDYIL_SPEECH_" + System.currentTimeMillis() + "_" + i);
        }
    }

    private void dispatchJs(String callback, String message) {
        if (webView == null) return;
        String quoted = JSONObject.quote(message == null ? "" : message);
        webView.post(() -> {
            if (webView != null) {
                webView.evaluateJavascript(
                        "if(window." + callback + ")window." + callback + "(" + quoted + ");",
                        null);
            }
        });
    }

    private static String safeMessage(Exception e) {
        String message = e.getMessage();
        return message == null || message.trim().isEmpty()
                ? e.getClass().getSimpleName() : message;
    }

    public final class ArdyilNativeBridge {
        @JavascriptInterface
        public void testMic() {
            runOnUiThread(() -> requestNativeMic(false));
        }

        @JavascriptInterface
        public void startRecording() {
            runOnUiThread(() -> requestNativeMic(true));
        }

        @JavascriptInterface
        public void stopRecording() {
            runOnUiThread(MainActivity.this::stopNativeRecording);
        }

        @JavascriptInterface
        public void speak(String text, String language, float rate, int repeats) {
            runOnUiThread(() -> speakNative(text, language, rate, repeats));
        }

        @JavascriptInterface
        public void stopSpeech() {
            runOnUiThread(() -> {
                if (textToSpeech != null) textToSpeech.stop();
            });
        }

        @JavascriptInterface
        public void shareText(String title, String text) {
            runOnUiThread(() -> {
                try {
                    Intent send = new Intent(Intent.ACTION_SEND);
                    send.setType("text/plain");
                    send.putExtra(Intent.EXTRA_SUBJECT,
                            title == null || title.trim().isEmpty() ? "ARDYIL" : title.trim());
                    send.putExtra(Intent.EXTRA_TEXT, text == null ? "" : text);
                    startActivity(Intent.createChooser(send, "Bagikan ARDYIL"));
                } catch (Exception e) {
                    dispatchJs("ardyilNativeShareError",
                            "Menu berbagi tidak tersedia pada perangkat ini. " + safeMessage(e));
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NATIVE_AUDIO) {
            boolean granted = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean start = pendingNativeStart;
            boolean test = pendingNativeMicTest;
            pendingNativeStart = false;
            pendingNativeMicTest = false;
            if (granted) {
                if (start) startNativeRecording();
                else if (test) dispatchJs("ardyilNativeMicReady", "");
            } else {
                dispatchJs("ardyilNativeRecordError",
                        "Izin mikrofon belum diberikan. Buka Setelan Android → Aplikasi → ARDYIL → Izin → Mikrofon, lalu pilih Izinkan.");
            }
            return;
        }

        if (requestCode != REQUEST_WEB_AUDIO) return;
        PermissionRequest request = pendingAudioRequest;
        pendingAudioRequest = null;
        if (request == null) return;
        boolean granted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (granted && !isFinishing() && !isDestroyed()) {
            request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
        } else {
            request.deny();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (pendingAudioRequest != null) {
            pendingAudioRequest.deny();
            pendingAudioRequest = null;
        }
        if (nativeRecording) {
            try { mediaRecorder.stop(); } catch (Exception ignored) {}
        }
        releaseRecorder();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        if (webView != null) {
            webView.stopLoading();
            webView.removeJavascriptInterface("ArdyilNative");
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}