# Setup Firebase untuk CatatanKu Cloud

Kode aplikasi untuk Tugas 2–5 sudah dilengkapi. Langkah berikut tetap harus dilakukan dari akun Firebase milik pemilik proyek.

## 1. Buat proyek dan daftarkan aplikasi Android

1. Buka https://console.firebase.google.com/ lalu buat proyek baru.
2. Tambahkan aplikasi Android.
3. Isi package name persis `com.example.ch11`.
4. Unduh `google-services.json` dari Firebase Console.
5. Ganti file placeholder `app/google-services.json` dengan file yang baru diunduh. Jangan ubah nama atau lokasinya.

## 2. Aktifkan Email/Password

1. Firebase Console → Authentication → Get started.
2. Buka Sign-in method.
3. Aktifkan provider Email/Password, lalu simpan.

## 3. Buat Firestore

1. Firebase Console → Firestore Database → Create database.
2. Pilih Production mode.
3. Pilih lokasi yang dekat dengan pengguna, misalnya `asia-southeast2` jika tersedia.
4. Setelah database dibuat, buka tab Rules.
5. Salin seluruh isi file `firestore.rules` dari proyek ini, lalu klik Publish.

Rules membatasi akses ke `users/{uid}/notes/{noteId}` hanya untuk pemilik UID tersebut. Dokumen juga harus berisi enam field dengan tipe yang benar.

## 4. Aktifkan Google Sign-In (Tugas Tantangan)

1. Authentication → Sign-in method → aktifkan Google.
2. Isi email dukungan proyek jika diminta.
3. Di terminal Android Studio jalankan `./gradlew signingReport` (Windows: `gradlew.bat signingReport`).
4. Salin SHA-1 dari varian debug ke Project settings → Your apps → aplikasi Android.
5. Unduh ulang `google-services.json` dan timpa file di `app/`.
6. Sinkronkan Gradle, lalu jalankan aplikasi pada emulator dengan Google Play services dan akun Google yang sudah login.

## 5. Jalankan dan uji

Dari folder proyek:

```bash
./gradlew testDebugUnitTest
```

Di Windows:

```bat
gradlew.bat testDebugUnitTest
```

Uji alur registrasi, logout, login kembali, pembuatan/perubahan/penghapusan catatan, perubahan dokumen dari Firebase Console, dan mode offline. Untuk bukti keamanan, gunakan Rules Playground:
- UID A membaca `/users/UID_A/notes/ID_CATATAN` → Allowed.
- UID B membaca path yang sama → Denied.

## Catatan penting

`app/google-services.json` pada arsip ini masih file placeholder. Login dan koneksi ke Firebase sungguhan belum dapat dipakai sampai file tersebut diganti dan provider/database diaktifkan. Pembuatan proyek Firebase, aktivasi provider, publikasi Rules, pengujian dua akun, dan tangkapan layar Rules Playground memerlukan akses ke akun Firebase serta emulator/perangkat Anda.
