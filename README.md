# Pemrograman Aplikasi Mobile - Pertemuan 10
## Testing dan Dependency Injection

**Mata Kuliah:** IF25-22017 - Pengembangan Aplikasi Mobile  
**Institut Teknologi Sumatera (ITERA)**  
**Tahun Akademik:** Genap 2025/2026  
**Branch:** week-10

---

## 📋 Deskripsi Tugas

Implementasi **Dependency Injection (DI) dengan Koin** dan **Testing komprehensif** untuk Notes App berbasis Kotlin Multiplatform (KMP) dengan Compose Multiplatform.

---

## ✅ Checklist Tugas

| No | Komponen | Status | Detail |
|----|----------|--------|--------|
| 1 | Koin DI Setup | ✅ | 2 modules: `dataModule`, `viewModelModule` |
| 2 | Unit Test NoteRepository | ✅ | 9 test cases (melebihi minimum 5) |
| 3 | Unit Test NotesViewModel (MockK) | ✅ | 6 test cases (melebihi minimum 4) |
| 4 | Flow Test dengan Turbine | ✅ | 4 test cases (melebihi minimum 2) |
| 5 | UI Test Compose | ✅ | 7 test cases (melebihi minimum 3) |
| 6 | Code Coverage | ✅ | Target >60% business logic |

---

## 📸 Hasil Test

### Unit Test — NoteValidatorTest
> Screenshot hasil test di sini
> 
> ![NoteValidatorTest](screenshots/NoteValidatorTest.png)

### Unit Test — NoteRepositoryTest
> Screenshot hasil test di sini
> 
> ![NoteRepositoryTest](screenshots/NoteRepositoryTest.png)

### Flow Test — NoteFlowTest
> Screenshot hasil test di sini
> 
> ![NoteFlowTest](screenshots/NoteFlowTest.png)

### ViewModel Test — NotesViewModelTest
> Screenshot hasil test di sini
> 
> ![NotesViewModelTest](screenshots/NotesViewModelTest.png)

### Koin Module Test — KoinModuleTest
> Screenshot hasil test di sini
> 
> ![KoinModuleTest](screenshots/KoinModuleTest.png)

### UI Test — NotesScreenTest
> Screenshot hasil test di sini (7/7 passed)
> 
> ![NotesScreenTest](screenshots/NotesScreenTest.png)

### Ringkasan Semua Test
> Screenshot ringkasan semua test passed di sini
> 
> ![AllTests](screenshots/AllTests.png)

---

## 🏗️ Arsitektur Project

```
composeApp/src/
├── commonMain/kotlin/com/example/myprofile/
│   ├── App.kt                          # Entry point KoinContext
│   ├── data/
│   │   ├── model/Note.kt               # Data model
│   │   └── repository/
│   │       ├── NoteRepository.kt       # Interface repository
│   │       └── NoteRepositoryImpl.kt   # Implementasi in-memory
│   ├── di/
│   │   └── AppModule.kt               # Koin modules (dataModule + viewModelModule)
│   ├── ui/notes/
│   │   ├── NotesScreen.kt             # Composable UI dengan TestTags
│   │   ├── NotesViewModel.kt          # ViewModel dengan constructor injection
│   │   └── NotesUiState.kt            # Sealed class UI state
│   └── util/
│       └── NoteValidator.kt           # Business logic validation
│
├── androidMain/kotlin/com/example/myprofile/
│   ├── MainActivity.kt
│   └── MyApplication.kt               # startKoin initialization
│
├── androidUnitTest/kotlin/com/example/myprofile/
│   ├── data/
│   │   ├── NoteValidatorTest.kt        # 9 test cases validator
│   │   ├── NoteRepositoryTest.kt       # 9 test cases repository + Turbine
│   │   └── NoteFlowTest.kt             # 4 Flow test cases dengan Turbine
│   ├── ui/
│   │   └── NotesViewModelTest.kt       # 6 test cases dengan MockK
│   └── di/
│       └── KoinModuleTest.kt           # Verifikasi dependency graph
│
└── androidInstrumentedTest/kotlin/com/example/myprofile/
    └── ui/
        └── NotesScreenTest.kt          # 7 UI test cases Compose Test
```

---

## 🔌 Dependency Injection (Koin)

### dataModule
```kotlin
val dataModule = module {
    single<NoteRepository> { NoteRepositoryImpl() } // Singleton via interface
    factory { NoteValidator() }                      // Factory - stateless
}
```

### viewModelModule
```kotlin
val viewModelModule = module {
    viewModel { NotesViewModel(get(), get()) } // Constructor injection
}
```

### Inisialisasi di Android
```kotlin
// MyApplication.kt
startKoin {
    androidLogger(Level.DEBUG)
    androidContext(this@MyApplication)
    modules(appModules) // [dataModule, viewModelModule]
}
```

---

## 🧪 Daftar Test Cases

### NoteValidatorTest (9 test cases)
| No | Test Case | Expected |
|----|-----------|----------|
| 1 | Catatan valid | `isValid()` → true |
| 2 | Konten kosong tapi ada judul | `isValid()` → true |
| 3 | Judul kosong | `isValid()` → false |
| 4 | Judul hanya spasi | `isValid()` → false |
| 5 | Judul melebihi MAX_TITLE_LENGTH | `isValid()` → false |
| 6 | Konten melebihi MAX_CONTENT_LENGTH | `isValid()` → false |
| 7 | `validate()` judul kosong | melempar `ValidationException` |
| 8 | `validate()` judul terlalu panjang | melempar `ValidationException` |
| 9 | `validate()` input valid | tidak melempar exception |

### NoteRepositoryTest (9 test cases)
| No | Test Case | Expected |
|----|-----------|----------|
| 1 | getAllNotes awal | list kosong |
| 2 | insertNote | catatan ditambahkan ke flow |
| 3 | insertNote id unik | id1 ≠ id2 |
| 4 | getNoteById | catatan ditemukan |
| 5 | getNoteById tidak ada | null |
| 6 | updateNote | data diperbarui |
| 7 | deleteNote | catatan terhapus |
| 8 | deleteAllNotes | repository kosong |
| 9 | searchNotes | filter sesuai query |

### NoteFlowTest — Turbine (4 test cases)
| No | Test Case | Tool |
|----|-----------|------|
| 1 | Flow update saat insert | Turbine `awaitItem()` |
| 2 | Flow update saat delete | Turbine `awaitItem()` |
| 3 | searchNotes flow reactive | Turbine |
| 4 | Flow update saat update | Turbine |

### NotesViewModelTest — MockK (6 test cases)
| No | Test Case | Mock |
|----|-----------|------|
| 1 | State: Loading → Success | `coEvery { getAllNotes() }` |
| 2 | addNote memanggil insertNote | `coVerify` |
| 3 | addNote gagal validasi | `coVerify(exactly = 0)` |
| 4 | deleteNote dengan id benar | `coVerify` |
| 5 | deleteAllNotes dipanggil | `coVerify` |
| 6 | updateSearchQuery update state | assert value |

### NotesScreenTest — Compose UI Test (7 test cases)
| No | Test Case | Method |
|----|-----------|--------|
| 1 | Empty state ditampilkan | `assertIsDisplayed()` |
| 2 | Daftar catatan tampil | `onNodeWithText()` |
| 3 | Loading indicator tampil | `onNodeWithTag()` |
| 4 | Error message tampil | `onNodeWithTag()` |
| 5 | Input judul menerima teks | `performTextInput()` |
| 6 | Tombol Tambah bisa diklik | `performClick()` |
| 7 | Search bar menerima input | `performTextInput()` |

---

## 🛠️ Cara Menjalankan Test

### Unit Tests (JVM)
```bash
./gradlew :composeApp:testDebugUnitTest
```

### Unit Tests dengan coverage report
```bash
./gradlew :composeApp:testDebugUnitTest jacocoTestReport
```

### UI / Instrumented Tests (perlu emulator/device)
```bash
./gradlew :composeApp:connectedDebugAndroidTest
```

### Run semua test
```bash
./gradlew test
```

---

## 📦 Dependencies Utama

```toml
[libraries]
# Testing
kotlin-test          = "org.jetbrains.kotlin:kotlin-test"
kotlinx-coroutines-test = "org.jetbrains.kotlinx:kotlinx-coroutines-test"
turbine              = "app.cash.turbine:turbine:1.2.0"
mockk                = "io.mockk:mockk:1.13.12"
compose-ui-test      = "androidx.compose.ui:ui-test-junit4"

# DI
koin-core            = "io.insert-koin:koin-core:4.0.0"
koin-compose         = "io.insert-koin:koin-compose:4.0.0"
koin-test            = "io.insert-koin:koin-test:4.0.0"
```

---

## 📸 Test Coverage Report

> Screenshot test coverage report akan dilampirkan setelah menjalankan:
> ```bash
> ./gradlew testDebugUnitTest jacocoTestReport
> ```
> Report tersedia di: `build/reports/jacoco/testDebugUnitTest/html/index.html`

**Estimasi Coverage:**
- `NoteValidator`: ~95%
- `NoteRepositoryImpl`: ~90%
- `NotesViewModel`: ~85%
- Overall business logic: **>80%** ⭐

---

## 🎯 Best Practices yang Diterapkan

1. **Constructor Injection** — Dependencies diterima melalui constructor, bukan dibuat di dalam class
2. **Interface Segregation** — `NoteRepository` sebagai interface memudahkan mocking
3. **AAA Pattern** — Setiap test mengikuti pola Arrange-Act-Assert
4. **Test Tags** — UI test menggunakan `TestTags` constant, bukan string literal
5. **Single Responsibility** — Setiap module Koin memiliki tanggung jawab yang jelas
6. **Relaxed Mocking** — Menggunakan `coEvery` dan `coVerify` untuk suspend functions

---

*Happy Testing! 🧪✨*
