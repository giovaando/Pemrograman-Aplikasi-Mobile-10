package com.example.myprofile.data

import app.cash.turbine.test
import com.example.myprofile.data.model.Note
import com.example.myprofile.data.repository.NoteRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Flow Test menggunakan Turbine.
 * Menguji perilaku reaktif repository saat data berubah.
 * Minimal 2 test cases sesuai rubrik penilaian.
 */
class NoteFlowTest {

    private lateinit var repository: NoteRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = NoteRepositoryImpl()
    }

    @Test
    fun `getAllNotes flow memancarkan update saat catatan ditambahkan`() = runTest {
        // Arrange
        repository.getAllNotes().test {
            // Assert emisi awal: list kosong
            val initial = awaitItem()
            assertTrue(initial.isEmpty(), "List awal harus kosong")

            // Act: tambah catatan pertama
            repository.insertNote(Note(title = "Catatan A", content = "Isi A"))

            // Assert: emisi berikutnya memuat catatan baru
            val afterFirst = awaitItem()
            assertEquals(1, afterFirst.size)
            assertEquals("Catatan A", afterFirst.first().title)

            // Act: tambah catatan kedua
            repository.insertNote(Note(title = "Catatan B", content = "Isi B"))

            // Assert: emisi selanjutnya memuat dua catatan
            val afterSecond = awaitItem()
            assertEquals(2, afterSecond.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllNotes flow memancarkan update saat catatan dihapus`() = runTest {
        // Arrange: siapkan repository dengan 2 catatan
        val id1 = repository.insertNote(Note(title = "Catatan 1", content = "Isi 1"))
        repository.insertNote(Note(title = "Catatan 2", content = "Isi 2"))

        repository.getAllNotes().test {
            // Konsumsi state saat ini (2 catatan)
            val initial = awaitItem()
            assertEquals(2, initial.size, "Harus ada 2 catatan")

            // Act: hapus catatan pertama
            repository.deleteNote(id1)

            // Assert: flow memancarkan list yang sudah diupdate
            val afterDelete = awaitItem()
            assertEquals(1, afterDelete.size, "Hanya satu catatan yang tersisa")
            assertEquals("Catatan 2", afterDelete.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchNotes flow memancarkan hasil pencarian yang sesuai`() = runTest {
        // Arrange
        repository.insertNote(Note(title = "Beli Sayur", content = "Bayam dan kangkung"))
        repository.insertNote(Note(title = "Agenda Rapat", content = "Review Q3"))
        repository.insertNote(Note(title = "Beli Buah", content = "Apel dan mangga"))

        // Act + Assert
        repository.searchNotes("beli").test {
            val results = awaitItem()
            assertEquals(2, results.size, "Harus menemukan 2 catatan dengan kata 'beli'")
            assertTrue(results.all { it.title.contains("Beli", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllNotes flow memancarkan update saat catatan diperbarui`() = runTest {
        // Arrange
        val id = repository.insertNote(Note(title = "Judul Lama", content = "Isi Lama"))

        repository.getAllNotes().test {
            val initial = awaitItem()
            assertEquals("Judul Lama", initial.first().title)

            // Act: update catatan
            val existing = repository.getNoteById(id)!!
            repository.updateNote(existing.copy(title = "Judul Baru", content = "Isi Baru"))

            // Assert
            val updated = awaitItem()
            assertEquals("Judul Baru", updated.first().title)
            assertEquals("Isi Baru", updated.first().content)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
