# ARDYIL English Dictionary — Android

Proyek Android native untuk membungkus kamus offline ARDYIL versi 2.0.

## Fitur
- Kamus HTML dan database kosakata disimpan di `app/src/main/assets/index.html`.
- Tidak membutuhkan koneksi internet saat aplikasi digunakan.
- WebView memakai `WebViewAssetLoader` dengan origin HTTPS lokal (`appassets.androidplatform.net`) agar `getUserMedia()` dapat digunakan.
- Izin `RECORD_AUDIO` Android diminta pada saat pengguna mengetes atau memulai rekaman.
- Akses kamera dan izin web lain tidak diberikan.
- Data favorit dan data lokal tersimpan melalui penyimpanan WebView di perangkat.

## Membuat APK dengan Android Studio

1. Ekstrak ZIP proyek ini di komputer.
2. Pasang Android Studio versi stabil dan Android SDK Platform 35.
3. Buka Android Studio → **Open** → pilih folder `ARDYIL_English_Dictionary_Android`.
4. Tunggu Gradle Sync selesai. Pastikan komputer terhubung ke internet saat pertama kali mengambil Android Gradle Plugin dan library AndroidX.
5. Pilih **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
6. APK debug akan muncul di:
   `app/build/outputs/apk/debug/app-debug.apk`

Untuk APK release bertanda tangan, pilih **Build → Generate Signed Bundle / APK** dan buat keystore milik sendiri. Jangan membagikan file keystore atau password-nya.

## Pengujian mikrofon di HP

1. Instal APK hasil build.
2. Buka aplikasi dan masuk ke tab **Latihan Bicara**.
3. Tekan **Tes & izinkan mikrofon**.
4. Saat dialog Android muncul, pilih **Izinkan saat menggunakan aplikasi**.
5. Mulai rekam dan putar kembali hasilnya.

Jika izin pernah ditolak, buka Setelan Android → Aplikasi → ARDYIL English Dictionary → Izin → Mikrofon.

## Konfigurasi teknis
- Application ID: `com.ardyil.dictionary`
- Min SDK: 23 (Android 6.0)
- Target / Compile SDK: 35
- Java: 11
- Android Gradle Plugin: 8.7.3
- AndroidX WebKit: 1.12.1

Aplikasi tidak meminta izin INTERNET. Pertama kali build tetap memerlukan internet untuk mengunduh komponen build Android.

## Alternatif build otomatis

Folder `.github/workflows/build-apk.yml` berisi alur build otomatis. Setelah proyek ini diunggah ke repository GitHub, buka tab **Actions**, pilih **Build ARDYIL APK**, lalu tekan **Run workflow**. Setelah selesai, unduh artifact bernama `ARDYIL-English-Dictionary-debug-apk`; di dalamnya ada `app-debug.apk`.

## Fitur audio dan mikrofon — wajib dipertahankan

Audio pelafalan dan rekaman mikrofon adalah fitur inti ARDYIL. Jangan menghapus atau mengganti implementasi native Android saat menambahkan fitur lain.

- Sumber bridge Android: `native-android/MainActivity.java`.
- Halaman aplikasi memanggil `ArdyilNative` untuk Text-to-Speech, tes izin mikrofon, mulai rekam, dan hentikan rekaman.
- Perekaman menggunakan `MediaRecorder` format MPEG-4/AAC dan hasilnya dapat diputar melalui pemutar rekaman di aplikasi.
- Mesin pengucapan menggunakan Android `TextToSpeech`. Paket suara English mungkin harus tersedia di pengaturan Text-to-Speech Android.
- Izin `android.permission.RECORD_AUDIO` wajib tetap ada.

Sebelum build, jalankan pemeriksaan: `bash scripts/check-audio-core.sh .`. Pemeriksaan ini juga dijalankan otomatis oleh GitHub Actions dan akan menggagalkan build bila komponen audio inti hilang.

Setiap perubahan kode audio tetap perlu dites pada perangkat Android sungguhan: izinkan mikrofon, rekam minimal 3 detik, hentikan, putar hasil rekaman, dengarkan contoh TTS English, dan uji tombol berhenti. Jangan mengklaim pengujian perangkat berhasil sebelum benar-benar dilakukan.
