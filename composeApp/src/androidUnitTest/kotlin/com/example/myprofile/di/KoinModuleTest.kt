package com.example.myprofile.di

import com.example.myprofile.data.repository.NoteRepository
import com.example.myprofile.ui.notes.NotesViewModel
import com.example.myprofile.util.NoteValidator
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Test untuk memverifikasi konfigurasi Koin DI.
 * Memastikan semua dependency dapat di-resolve dengan benar.
 */
class KoinModuleTest : KoinTest {

    @BeforeTest
    fun setup() {
        startKoin {
            modules(appModules)
        }
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `NoteRepository dapat di-resolve dari Koin`() {
        // Act
        val repository: NoteRepository = get()

        // Assert
        assertNotNull(repository, "NoteRepository harus bisa di-inject")
    }

    @Test
    fun `NoteValidator dapat di-resolve dari Koin`() {
        // Act
        val validator: NoteValidator = get()

        // Assert
        assertNotNull(validator, "NoteValidator harus bisa di-inject")
    }
}
