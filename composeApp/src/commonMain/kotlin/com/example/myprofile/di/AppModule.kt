package com.example.myprofile.di

import com.example.myprofile.data.repository.NoteRepository
import com.example.myprofile.data.repository.NoteRepositoryImpl
import com.example.myprofile.ui.notes.NotesViewModel
import com.example.myprofile.util.NoteValidator
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Module Koin untuk layer data.
 * Mendefinisikan singleton untuk repository agar satu instance
 * digunakan di seluruh aplikasi (konsistensi data).
 */
val dataModule = module {
    // NoteRepository sebagai singleton dengan interface
    // Memungkinkan penggantian implementasi tanpa ubah consumer
    single<NoteRepository> { NoteRepositoryImpl() }

    // NoteValidator sebagai factory - stateless, bisa dibuat ulang
    factory { NoteValidator() }
}

/**
 * Module Koin untuk layer ViewModel.
 * viewModel { } memastikan lifecycle ViewModel dikelola dengan benar.
 */
val viewModelModule = module {
    viewModel { NotesViewModel(get(), get()) }
}

/**
 * Daftar semua module yang digunakan aplikasi.
 * Dideklarasikan sebagai list agar mudah ditambahkan ke startKoin.
 */
val appModules = listOf(dataModule, viewModelModule)
