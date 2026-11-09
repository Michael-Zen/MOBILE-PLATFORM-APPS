# Setup Firebase untuk Ch12

Project ini menggunakan package Android `com.example.ch12`. File
`app/google-services.json` yang disertakan adalah placeholder, bukan konfigurasi
Firebase yang dapat dipakai untuk login atau upload.

## Firebase sungguhan

1. Buka Firebase Console dan gunakan proyek Ch11 yang sama (atau proyek baru).
2. Tambahkan aplikasi Android dengan package name `com.example.ch12`.
3. Unduh `google-services.json` dan timpa file `app/google-services.json`.
4. Aktifkan Authentication provider yang digunakan (Email/Password dan Google
   jika ingin menguji Google Sign-In).
5. Aktifkan Cloud Firestore dan Cloud Storage. Storage memerlukan paket Blaze;
   periksa ketentuan billing Firebase dan buat budget alert sebelum mengaktifkan.
6. Deploy `firestore.rules` dan `storage.rules` dari root project:
   `firebase deploy --only firestore:rules,storage`.
7. Jalankan `./gradlew testDebugUnitTest`, lalu jalankan aplikasi pada perangkat.

## Tanpa billing: Firebase Emulator Suite

1. Pasang Firebase CLI dan Java 11 atau lebih baru.
2. Dari folder project, jalankan `firebase emulators:start --project demo-catatanku`.
3. Set `catatanku.useFirebaseEmulator=true` di `gradle.properties`.
4. Jalankan aplikasi di Android Emulator. Konfigurasi emulator diarahkan ke
   `10.0.2.2` oleh FirebaseModule.
5. Untuk perangkat fisik, sesuaikan host emulator di `di/FirebaseModule.kt`
   dengan alamat IP komputer.

Jangan membagikan file `google-services.json` yang berisi konfigurasi proyek
pribadi ke repositori publik jika kebijakan proyek melarangnya.
