# Ch12 Starter — CatatanKu Foto: Media, Kamera & Permissions

Titik awal Praktikum Pertemuan 12. Isinya **CatatanKu Cloud (Ch11) yang sudah
selesai** (login, catatan real-time di Firestore, Security Rules), ditambah
kerangka fitur **foto profil**. Layar Profil, layar kamera (CameraX), avatar di
daftar catatan, dan navigasinya **sudah jadi**. Yang **belum** ada adalah
bagian yang menyentuh inti Bab 12: Photo Picker, izin kamera, kompresi gambar,
upload ke Cloud Storage, dan aturan keamanannya.

Semua bagian yang harus dikerjakan ditandai dengan komentar `// TODO [Tx.y]`.

> Jawaban lengkap ada di `praktikum/ch12`. Kerjakan dulu sendiri, baru
> bandingkan. Jangan menyalin sebelum semua TODO selesai.

```
Foto (Uri) ──▶ Kompres & perkecil ──▶ Cloud Storage ──URL──▶ Firestore: users/{uid}.photoUrl
 Photo Picker     ImageCompressor       PhotoStorage          ProfileRepository
 atau CameraX     (T4.1, T4.2)          (T4.3)                (T4.5)
```

---

## Yang Sudah Disiapkan

- **Gradle lengkap**: `firebase-storage`, CameraX 1.3.4, `exifinterface`, Coil 3
  (Ch09), dan `concurrent-futures-ktx`. Kamu tidak perlu mengubah Gradle.
- **Interface** `ProfileRepository`, `PhotoStorage`, `ImageCompressor`, beserta
  `@Binds` di `di/RepositoryModule.kt`. `FirebaseModule` menyediakan
  `FirebaseStorage`.
- **`ui/profile/ProfileScreen.kt`**: preview bulat, tombol Galeri/Kamera, progres
  kompres + upload, dialog rationale dan dialog Pengaturan. Murni presentasional.
- **`ui/camera/CameraScreen.kt`**: preview CameraX, kamera depan/belakang, ambil
  foto ke direktori cache. Bacalah, terutama `bindToLifecycle` dan `takePicture`.
- **`ui/camera/CameraPermissions.kt`**: membaca fakta dari sistem
  (`hasCameraPermission`, `shouldShowCameraRationale`, `openAppSettings`).
- **`ui/profile/ProfilePhoto.kt`**: `AsyncImage` dengan placeholder, error, dan
  kunci cache. Dipakai di layar profil dan avatar di daftar catatan.
- Hasil kamera dikirim ke layar profil lewat `SavedStateHandle` (sudah jalan di
  `ProfileViewModel.init` dan `CatatanKuApp`).
- **Semua unit test** dan fake-nya (`FakeImageCompressor`, `FakePhotoStorage`,
  `FakeProfileRepository`).
- `firebase.json` untuk deploy rules dan **Firebase Emulator Suite**.

---

## TODO — Wajib Diselesaikan Mahasiswa

**Menemukan semua TODO:** Android Studio → **View → Tool Windows → TODO**
(`⌘6` di macOS, `Alt+6` di Windows/Linux). Hapus komentar `TODO` setelah
selesai, sehingga daftar itu **kosong** saat kamu selesai.

### Tugas 1 — Firebase (tanpa kode)

| ID | Tempat | Yang harus dikerjakan |
|---|---|---|
| [ ] **T1.1** | Firebase Console, proyek Ch11 | **Add app → Android**, package name **`com.example.ch12`** |
| [ ] **T1.2** | `app/google-services.json` | Unduh ulang → **timpa** placeholder. Pastikan `storage_bucket` terisi |
| [ ] **T1.3** | Console → **Storage** | **Get started** (lokasi sama dengan Firestore), *production mode* |

> **Cloud Storage butuh paket Blaze** (bayar sesuai pemakaian, tetap ada kuota
> gratis; pasang *budget alert* Rp0). Jika tidak bisa memakai kartu kredit,
> gunakan **Firebase Emulator Suite**: lihat bagian
> [Tanpa Paket Blaze](#tanpa-paket-blaze-firebase-emulator-suite).

### Tugas 2 — Photo Picker dan Preview (tanpa izin)

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [x] **T2.1** | `ui/profile/ProfileRoute.kt` | `rememberLauncherForActivityResult(PickVisualMedia())` → `onPhotoSelected(uri.toString())`; isi `launchPicker` |
| [x] **T2.2** | `ui/profile/ProfileViewModel.kt` | `onPhotoSelected()`: simpan ke `SavedStateHandle` + tampilkan preview, **belum** upload |

Setelah T2.1–T2.2, foto dari galeri dan dari kamera tampil sebagai preview.
Perhatikan: **tidak ada dialog izin** untuk galeri.

### Tugas 3 — Izin Kamera yang Benar

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [x] **T3.1** | `AndroidManifest.xml` | `<uses-permission CAMERA>` + `<uses-feature camera.any required=false>` |
| [x] **T3.2** | `util/CameraPermission.kt` | `cameraClickAction()` dan `permissionResultAction()` (fungsi murni, diuji) |
| [x] **T3.3** | `ui/profile/ProfileRoute.kt` | Launcher `RequestPermission()`, lalu `onTakePhoto` memakai `cameraClickAction` |

Kondisi awal: tombol **Kamera** membuka layar kamera, yang langsung menutup
lagi karena izin tidak ada (`CameraRoute` memeriksa ulang izin). Sebelum T3.1,
permintaan izin **ditolak tanpa dialog**: coba sendiri setelah T3.3 tetapi
sebelum T3.1.

```
Ketuk Kamera ─▶ sudah diizinkan? ──ya──▶ buka kamera
                     │ tidak
                     ▼
             perlu rationale? ──ya──▶ dialog alasan ─▶ "Lanjutkan" ─┐
                     │ tidak                       └▶ "Pakai galeri" │
                     ▼                                               ▼
             dialog sistem ──izinkan──▶ buka kamera
                     │ tolak
                     ├─ rationale ON  → Snackbar: "pakai galeri saja"   (ShowDenied)
                     └─ rationale OFF → dialog "Buka Pengaturan"        (ditolak permanen)
```

### Tugas 4 — Kompres, Upload dengan Progres, Simpan URL

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [x] **T4.1** | `util/ImageMath.kt` | `calculateInSampleSize()`: pangkat 2 terbesar, sisi terpendek tetap ≥ 400 |
| [x] **T4.2** | `data/photo/AndroidImageCompressor.kt` | Dekode dua tahap (`inJustDecodeBounds` → `inSampleSize`), perkecil ke 400 px, putar sesuai EXIF, JPEG 80 |
| [x] **T4.3** | `data/photo/FirebasePhotoStorage.kt` | `callbackFlow` atas `putBytes()`: progres → `Progress`, selesai → `Done(downloadUrl)`, `awaitClose { task.cancel() }` |
| [x] **T4.4** | `ui/profile/ProfileViewModel.kt` | `upload()`: kompres → kumpulkan Flow upload → simpan URL |
| [x] **T4.5** | `data/profile/FirestoreProfileRepository.kt` | `updatePhotoUrl()`: `set(..., SetOptions.merge())` **tanpa** `await()` |
| [x] **T4.6** | `storage.rules`, `firestore.rules` | Storage: hanya pemilik, < 1 MB, `image/jpeg`. Firestore: aturan `users/{userId}` + `isValidProfile()` |

Publish rules: salin ke **Console → Storage → Rules** dan **Firestore → Rules**,
atau `firebase deploy --only storage,firestore:rules`.

> **Mengapa upload di-`await` (lewat Flow), padahal penulisan Firestore tidak?**
> Firestore punya cache lokal dan antrean penulisan offline. Storage **tidak**:
> berkas harus sampai di server, baru URL-nya ada. Karena itu upload
> menampilkan progres, bisa dibatalkan, dan bisa gagal (tombol **Coba lagi**).

### Tugas 5 — Laporan di README

Isi bagian **[Laporan](#laporan-tugas-5)** di bawah: gambarkan alur izin
aplikasimu dan jelaskan mengapa galeri tidak membutuhkan izin.

### Tugas 6 (Tantangan) — Crop Persegi

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [x] **T6.1** | `util/ImageMath.kt` | `centerSquare()`: persegi terbesar di tengah gambar |
| [x] **T6.2** | `data/photo/AndroidImageCompressor.kt` | Pakai `centerSquare()` di `Bitmap.createBitmap(...)` sehingga hasil 400×400 |

Tantangan lanjutan (opsional, tanpa tes): biarkan pengguna **menggeser** area
crop sebelum upload.

### Aturan yang harus dipatuhi

- **Jangan meminta `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE`.** Galeri lewat
  Photo Picker.
- **Jangan meminta izin kamera saat aplikasi dibuka.** Hanya saat tombol Kamera
  diketuk (T3.3).
- **Foto tidak boleh disimpan di dokumen Firestore** (Base64 dsb.). Firestore
  hanya menyimpan URL.
- **Jangan upload foto asli.** Selalu lewat `ImageCompressor` (hasil < 100 KB).
- **ViewModel tidak boleh meng-import `com.google.firebase` atau `android.graphics`.**
- Rules akhir **tidak boleh** hanya `request.auth != null`.
- **Jangan commit gambar besar** ke Git (foto uji, tangkapan layar asli > 1 MB).

### Membaca hasil tes

```bash
./gradlew testDebugUnitTest      # tanpa emulator, kamera, atau Firebase
```

| Berkas tes | Kondisi awal | Hijau setelah |
|---|---|---|
| Tes Ch11 (48), `ProfileMapperTest` (4), `StorageErrorsTest` (3), `SessionViewModelTest` (+1) | **hijau** | harus **tetap hijau** |
| `CameraPermissionTest` (6) | 6 merah | **T3.2** |
| `ImageMathTest` (8) | 5 merah | **T4.1** (2 tes inSampleSize) dan **T6.1** (3 tes centerSquare) |
| `ProfileViewModelTest` (11) | 8 merah | **T2.2** (2 tes preview) lalu **T4.4** (6 tes upload) |

Total **81** tes: 62 hijau dan 19 merah di awal. T2.1, T3.1, T3.3, T4.2, T4.3,
T4.5, dan T4.6 menyentuh Android/Firebase sungguhan, jadi buktikan lewat
checklist di bawah.

### Error yang sering muncul

| Gejala / pesan | Penyebab |
|---|---|
| Tombol Galeri tidak melakukan apa pun | T2.1 belum |
| Tombol Kamera membuka layar hitam yang langsung menutup | T3.3 belum (izin tidak diminta) |
| Dialog izin tidak pernah muncul, langsung "ditolak" | T3.1: CAMERA belum ada di manifest |
| *Belum diimplementasikan: TODO [T…]* | Stub yang disebut belum dikerjakan |
| *Cloud Storage belum diaktifkan, atau google-services.json bukan dari proyek ini* | T1.3 belum, atau JSON diunduh sebelum Storage aktif (unduh ulang) |
| *Upload ditolak server. Periksa storage.rules…* | Path rules ≠ `users/{uid}/profile.jpg`, berkas ≥ 1 MB, atau `contentType` bukan `image/jpeg` |
| Upload berhasil, tetapi avatar tidak berubah | T4.5 belum, atau `firestore.rules` belum punya aturan `users/{userId}` (T4.6). Lihat Logcat `FirestoreProfileRepo` |
| Foto dari kamera tampil miring setelah diupload | `readRotation()` tidak dipakai di T4.2 |
| `OutOfMemoryError` saat memilih foto besar | Dekode tanpa `inSampleSize` (T4.2 langkah 2) |

### Dianggap selesai jika semua ini benar

- [ ] Galeri: pilih foto **tanpa dialog izin** → preview → **Unggah** → progres
      0–100% → avatar di daftar catatan berubah.
- [ ] Kamera, pertama kali: dialog sistem muncul **setelah** tombol Kamera diketuk.
- [ ] Tolak sekali → Snackbar menawarkan galeri. Ketuk Kamera lagi → **dialog alasan**.
- [ ] Tolak lagi → ketuk Kamera → dialog **Buka Pengaturan** → aktifkan → kamera terbuka.
- [ ] Teks kompresi menunjukkan hasil **< 100 KB**; berkas di Console → Storage juga < 100 KB.
- [ ] Mode pesawat → Unggah → ±30 detik kemudian pesan error + tombol **Coba lagi**; online → berhasil.
- [ ] Firestore → Data: `users/{uid}` hanya berisi `photoUrl` dan `photoUpdatedAt`.
- [ ] Rules Playground Storage: `create` ke `users/<UID_A>/profile.jpg` sebagai akun B → **Denied**.
- [ ] Jendela TODO kosong; `./gradlew testDebugUnitTest` hijau semua (**81** tes).

**Pengumpulan:** push ke branch `pertemuan-12` → kumpulkan link di LMS sebelum
pertemuan berikutnya. Sertakan tangkapan layar dialog izin, progres upload, dan
Rules Playground (perkecil dulu; jangan commit gambar besar).

---

## Laporan (Tugas 5)

> Laporan berikut menjelaskan implementasi yang telah dibuat.

### Alur izin kamera aplikasiku

Saat pengguna mengetuk tombol Kamera, aplikasi memeriksa status izin melalui
`hasCameraPermission()` dan `shouldShowCameraRationale()`. Fungsi
`cameraClickAction()` menentukan tindakan berikutnya. Jika izin sudah diberikan,
aplikasi langsung membuka kamera. Jika izin belum ada dan belum pernah ditolak,
aplikasi meminta izin melalui `RequestPermission()`. Jika pengguna pernah menolak,
aplikasi menampilkan dialog alasan terlebih dahulu. Tombol Lanjutkan pada dialog
tersebut akan meminta izin ke sistem, sedangkan tombol Pakai galeri membuka Photo
Picker.

Setelah dialog sistem muncul, `permissionResultAction()` memeriksa hasilnya.
Jika izin diberikan, kamera dibuka. Jika ditolak dan sistem masih mengizinkan
rationale, aplikasi menampilkan Snackbar yang menawarkan galeri. Jika ditolak
secara permanen, aplikasi menampilkan dialog Pengaturan agar pengguna dapat
mengaktifkan izin kamera secara manual. Dengan alur ini, izin hanya diminta ketika
fitur kamera benar-benar digunakan.

### Mengapa galeri tidak membutuhkan izin?

Aplikasi menggunakan Android Photo Picker melalui
`ActivityResultContracts.PickVisualMedia()`. Pemilihan foto didelegasikan kepada
komponen sistem Android, sehingga aplikasi tidak perlu meminta akses ke seluruh
koleksi foto pengguna. Setelah pengguna memilih gambar, sistem memberikan sebuah
`content://` URI yang dapat dibaca aplikasi untuk gambar tersebut. Akses ini
bersifat terbatas pada item yang dipilih, bukan akses menyeluruh ke galeri.

Cara ini lebih aman dibandingkan meminta `READ_MEDIA_IMAGES` atau izin
penyimpanan lama, karena aplikasi hanya memperoleh akses ke foto yang memang
dipilih pengguna. Pengguna tetap memegang kendali atas foto mana yang dibagikan.

### Hasil kompresi

Sebagai contoh, foto dengan resolusi 4000 × 3000 memiliki 12.000.000 piksel.
Jika didekode sebagai bitmap ARGB_8888, kebutuhan memorinya sekitar
4000 × 3000 × 4 = 48.000.000 byte atau sekitar 45,8 MiB. Ukuran berkas JPEG
aslinya bisa berbeda karena bergantung pada tingkat kompresi.

Aplikasi membaca dimensi gambar terlebih dahulu, kemudian mendekode dengan
`inSampleSize` agar penggunaan memori lebih kecil. Gambar dipotong menjadi persegi
di bagian tengah, diputar sesuai informasi EXIF, lalu diubah menjadi maksimal
400 × 400 piksel dan disimpan sebagai JPEG dengan kualitas 80. Hasil kompresi
inilah yang diunggah ke Cloud Storage; foto asli tidak dikirim.

---

## Tanpa Paket Blaze: Firebase Emulator Suite

1. Pasang [Firebase CLI](https://firebase.google.com/docs/cli) dan Java 11+.
2. Di folder ini: `firebase emulators:start --project demo-catatanku`
   (Auth 9099, Firestore 8080, Storage 9199, UI di http://localhost:4000).
3. Di `gradle.properties`: `catatanku.useFirebaseEmulator=true` → Sync → Run di
   **emulator Android** (alamat `10.0.2.2`). Untuk HP fisik, ganti
   `EMULATOR_HOST` di `di/FirebaseModule.kt` dengan IP komputer.
4. Akun dan data di emulator hilang saat emulator dimatikan. Rules dibaca dari
   `firestore.rules` dan `storage.rules` di folder ini, jadi T4.6 tetap bisa diuji.

---

## Cara Membuka di Android Studio

1. **File → Open** → pilih folder `praktikum/ch12_starter/`
2. Gradle sync → pilih device/emulator → **Run ▶**. Aplikasi langsung berjalan;
   avatar di kanan atas daftar catatan membuka layar **Foto Profil**.
3. Uji kamera sebaiknya di **perangkat fisik**. Di emulator, atur
   *Extended controls → Camera* ke `VirtualScene` atau `Webcam0`.

> **Versi compiler sama dengan Ch03–Ch11**: Gradle 8.6, AGP 8.3.2, Kotlin
> 1.9.23, KSP `1.9.23-1.0.20`, Hilt 2.51.1, Firebase BoM 33.1.0. Ditambah
> CameraX 1.3.4, ExifInterface 1.3.7, Coil 3.0.4.

Materi konsep (model izin, delegasi, Activity Result API, URI/FileProvider,
scoped storage, memori gambar, berkas di cloud) ada di buku Bab 12 dan slide
`ch12`. Penjelasan tiap langkah ada di `praktikum/ch12/README.md`.

---

## Run the code

```bash
cd /Users/dhareva/Books/2026_MP/codes/praktikum/ch12_starter
./gradlew installDebug
adb shell am start -n com.example.ch12/.MainActivity
```


## Konfigurasi Firebase yang masih perlu dilakukan

Bagian kode aplikasi, Photo Picker, izin kamera, kompresi, upload, penyimpanan URL,
dan rules sudah diimplementasikan. Namun, konfigurasi yang terikat ke akun Firebase
tidak dapat dibuat dari project starter ini. Sebelum menjalankan aplikasi dengan
Firebase sungguhan, daftarkan aplikasi Android dengan package
`com.example.ch12`, aktifkan Cloud Storage, lalu unduh `google-services.json` milik
proyek tersebut dan timpa file placeholder di `app/`. Aktifkan metode login yang
digunakan aplikasi dan deploy `firestore.rules` serta `storage.rules`.

Cloud Storage pada proyek Firebase memerlukan paket Blaze. Jika tidak ingin
menghubungkan billing, gunakan Firebase Emulator Suite sesuai petunjuk di atas.
