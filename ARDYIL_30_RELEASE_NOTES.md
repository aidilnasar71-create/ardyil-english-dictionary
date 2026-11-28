# ARDYIL English Dictionary 30.0 — Catatan rilis

## Yang ditambahkan
- Dashboard Quest & Coin yang terintegrasi pada halaman Home.
- 30 level belajar bahasa Inggris berisi ungkapan, terjemahan, contoh penggunaan, dan kuis.
- Tiga misi harian: kuis tiga soal, review lima kosakata, dan cerita bilingual tiga adegan.
- Koin virtual dan XP dari misi serta penyelesaian level; pembukaan level berurutan memakai 20 koin.
- Progres, saldo koin, level terbuka, dan hadiah harian disimpan lokal di perangkat.
- Empat cerita animasi orisinal baru: The Paper Bridge, Little Music Makers, The Cloud Library, dan A Map of Kindness. Total 12 cerita tersedia dalam pemutar animasi vektor.
- Delapan preset tambahan untuk variasi piano, gitar, dan ukulele sintetis yang dihasilkan secara offline.
- Pemeriksaan otomatis konten 30 level, jumlah cerita, dan sintaks JavaScript pada workflow.

## Yang dipertahankan
- Kamus offline, pencarian, favorit, pola kalimat, audio pelafalan, kuis lama, Academy, Stories & Comics, novel, puzzle, dan progres lokal.
- Native Android microphone recording, pemutaran hasil rekaman, Text-to-Speech, callback, dan izin RECORD_AUDIO.

## Belum diaktifkan
- Google Play Billing dan pembelian koin belum aktif. Belum ada product ID Play Console, integrasi BillingClient, verifikasi pembelian, maupun konfigurasi backend resmi. Koin saat ini hanya poin virtual lokal, tidak dapat diuangkan atau dipindahkan.
- Google Sign-In dan cloud sync masih menunggu konfigurasi OAuth/Firebase resmi.
- Cerita lima menit yang tersedia adalah animasi vektor interaktif, bukan berkas video MP4. Berkas MP4/music pack berukuran besar perlu sumber media orisinal atau lisensi dan sebaiknya menjadi unduhan opsional.

## Status QA
- HTML dan seluruh JavaScript diperiksa sintaksnya sebelum build.
- Audio-core regression guard tetap wajib dijalankan.
- Build APK yang sukses tidak menggantikan uji fisik perangkat. Mikrofon, playback rekaman, TTS, penyimpanan progres, navigasi, serta responsivitas masih harus diverifikasi di HP Android 13.
