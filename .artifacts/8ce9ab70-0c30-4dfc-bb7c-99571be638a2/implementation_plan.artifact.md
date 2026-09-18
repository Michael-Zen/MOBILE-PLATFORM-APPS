# Implementation Plan - Tugas Pertemuan 5

Selesaikan tugas pertemuan 5 sesuai dengan instruksi di README.md, meliputi pembuatan grid foto di `ExploreScreen`, penambahan animasi transisi di `MainScreen`, dan konfigurasi Deep Link.

## Proposed Changes

### [Component: UI - Explore Screen]

#### [MODIFY] [ExploreScreen.kt](file:///C:/Users/USER ASUS/Documents/MOBILE PLATFORM APPS DEVELPMNT/ch05_starter/app/src/main/java/com/example/ch05starter/ExploreScreen.kt)
- Menambahkan import untuk `LazyVerticalGrid`, `GridCells`, `items`, `PaddingValues`, `Arrangement`, dan `dp`.
- Mengganti `Box` placeholder dengan `LazyVerticalGrid` 2 kolom yang merender `dummyPhotos`.
- Memberikan jarak (spacing) antar item sebesar `8.dp`.

### [Component: Navigation & Animation]

#### [MODIFY] [MainScreen.kt](file:///C:/Users/USER ASUS/Documents/MOBILE PLATFORM APPS DEVELPMNT/ch05_starter/app/src/main/java/com/example/ch05starter/MainScreen.kt)
- Menambahkan import untuk animasi (`fadeIn`, `fadeOut`, `slideInHorizontally`, `slideOutHorizontally`).
- Menambahkan `enterTransition` dan `exitTransition` pada setiap `composable` di `NavHost`.
- Menambahkan `deepLinks` pada `composable` untuk rute Detail.

### [Component: App Configuration]

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/USER ASUS/Documents/MOBILE PLATFORM APPS DEVELPMNT/ch05_starter/app/src/main/AndroidManifest.xml)
- Menambahkan `<intent-filter>` untuk mendukung skema deep link `myapp://article/{id}`.

## Verification Plan

### Automated Tests
- Menjalankan build Gradle untuk memastikan tidak ada error kompilasi: `./gradlew assembleDebug`

### Manual Verification
- Menjalankan aplikasi dan membuka tab "Eksplorasi" untuk memverifikasi grid foto.
- Berpindah antar tab dan navigasi ke Detail untuk melihat animasi transisi.
- Menguji Deep Link menggunakan perintah ADB:
  ```bash
  adb shell am start -W -a android.intent.action.VIEW -d "myapp://article/3" com.example.ch05starter
  ```
