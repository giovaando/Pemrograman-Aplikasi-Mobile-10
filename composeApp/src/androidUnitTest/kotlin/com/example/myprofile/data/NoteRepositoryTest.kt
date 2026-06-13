package com.example.myprofile.data

import com.example.myprofile.data.model.Note
import com.example.myprofile.data.repository.NoteRepositoryImpl
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit test untuk NoteRepositoryImpl.
 * Menggunakan Turbine untuk menguji Flow emissions secara reaktif.
 * Minimal 5 test cases sesuai rubrik penilaian.
 */
class NoteRepositoryTest {

    private lateinit var repository: NoteRepositoryImpl

    private val testNote1 = Note(title = "Catatan 1", content = "Isi catatan 1")
    private val testNote2 = Note(title = "Catatan 2", content = "Isi catatan 2")

    @BeforeTest
    fun setup() {
        // Arrange: buat repository baru (in-memory) untuk setiap test
        repository = NoteRepositoryImpl()
    }

    @Test
    fun `getAllNotes mengembalikan list kosong pada awalnya`() = runTest {
        // Arrange: repository sudah bersih (fresh dari setup)

        // Act + Assert menggunakan Turbine
        repository.getAllNotes().test {
            val items = awaitItem()
            assertTrue(items.isEmpty(), "Repository baru seharusnya kosong")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertNote menambahkan catatan ke repository`() = runTest {
        // Arrange
        repository.getAllNotes().test {
            awaitItem() // Konsumsi emisi awal (list kosong)

            // Act
            repository.insertNote(testNote1)

            // Assert: emisi berikutnya harus mengandung catatan baru
            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals("Catatan 1", updated.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertNote memberikan id unik untuk setiap catatan`() = runTest {
        // Arrange & Act
        val id1 = repository.insertNote(testNote1)
        val id2 = repository.insertNote(testNote2)

        // Assert
        assertTrue(id1 != id2, "Setiap catatan harus memiliki id unik")
        assertTrue(id1 > 0, "Id harus positif")
        assertTrue(id2 > 0, "Id harus positif")
    }

    @Test
    fun `getNoteById mengembalikan catatan yang benar`() = runTest {
        // Arrange
        val id = repository.insertNote(testNote1)

        // Act
        val found = repository.getNoteById(id)

        // Assert
        assertNotNull(found, "Catatan harus ditemukan berdasarkan id")
        assertEquals("Catatan 1", found.title)
        assertEquals("Isi catatan 1", found.content)
    }

    @Test
    fun `getNoteById mengembalikan null jika id tidak ditemukan`() = runTest {
        // Arrange: repository kosong

        // Act
        val found = repository.getNoteById(999L)

        // Assert
        assertNull(found, "Harus null untuk id yang tidak ada")
    }

    @Test
    fun `updateNote memperbarui catatan yang ada`() = runTest {
        // Arrange
        val id = repository.insertNote(testNote1)
        val original = repository.getNoteById(id)!!

        // Act
        repository.updateNote(original.copy(title = "Judul Diperbarui", content = "Isi baru"))

        // Assert
        val updated = repository.getNoteById(id)
        assertNotNull(updated)
        assertEquals("Judul Diperbarui", updated.title)
        assertEquals("Isi baru", updated.content)
    }

    @Test
    fun `deleteNote menghapus catatan yang sesuai`() = runTest {
        // Arrange
        val id1 = repository.insertNote(testNote1)
        repository.insertNote(testNote2)

        // Act
        repository.deleteNote(id1)

        // Assert
        repository.getAllNotes().test {
            val remaining = awaitItem()
            assertEquals(1, remaining.size, "Hanya satu catatan yang tersisa")
            assertEquals("Catatan 2", remaining.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteAllNotes mengosongkan repository`() = runTest {
        // Arrange
        repository.insertNote(testNote1)
        repository.insertNote(testNote2)

        // Act
        repository.deleteAllNotes()

        // Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty(), "Semua catatan harus terhapus")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchNotes mengembalikan catatan sesuai query`() = runTest {
        // Arrange
        repository.insertNote(Note(title = "Belanja Supermarket", content = "Sayur dan buah"))
        repository.insertNote(Note(title = "Meeting Tim", content = "Review sprint"))
        repository.insertNote(Note(title = "Olahraga", content = "Jogging pagi"))

        // Act + Assert dengan Turbine
        repository.searchNotes("belanja").test {
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("Belanja Supermarket", results.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchNotes dengan query kosong mengembalikan semua catatan`() = runTest {
        // Arrange
        repository.insertNote(testNote1)
        repository.insertNote(testNote2)

        // Act + Assert
        repository.searchNotes("").test {
            val results = awaitItem()
            assertEquals(2, results.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
