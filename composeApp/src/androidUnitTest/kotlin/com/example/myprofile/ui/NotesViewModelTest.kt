package com.example.myprofile.ui

import app.cash.turbine.test
import com.example.myprofile.data.model.Note
import com.example.myprofile.data.repository.NoteRepository
import com.example.myprofile.ui.notes.NotesUiState
import com.example.myprofile.ui.notes.NotesViewModel
import com.example.myprofile.util.NoteValidator
import com.example.myprofile.util.ValidationException
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Unit test untuk NotesViewModel menggunakan MockK dan Turbine.
 * Pola AAA (Arrange-Act-Assert) digunakan secara konsisten.
 * Minimal 4 test cases sesuai rubrik penilaian.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val mockRepository: NoteRepository = mockk()
    private val mockValidator: NoteValidator   = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val testNote = Note(id = 1L, title = "Test Note", content = "Test Content")

    @BeforeTest
    fun setup() {
        // Atur TestDispatcher sebagai Main dispatcher agar coroutine terkontrol
        Dispatchers.setMain(testDispatcher)
        // Default stub untuk repository
        coEvery { mockRepository.getAllNotes() } returns flowOf(listOf(testNote))
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state awal adalah Loading kemudian Success dengan data`() = runTest {
        // Arrange
        val viewModel = NotesViewModel(mockRepository, mockValidator)

        // Act + Assert
        viewModel.uiState.test {
            assertIs<NotesUiState.Loading>(awaitItem(), "State awal harus Loading")

            val success = awaitItem()
            assertIs<NotesUiState.Success>(success, "Harus Success setelah data dimuat")
            assertEquals(1, success.notes.size)
            assertEquals("Test Note", success.notes.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addNote memanggil insertNote pada repository dengan data yang tepat`() = runTest {
        // Arrange
        coEvery { mockRepository.getAllNotes() } returns flowOf(emptyList())
        coEvery { mockRepository.insertNote(any()) } returns 2L
        every { mockValidator.validate(any()) } returns Unit

        val viewModel = NotesViewModel(mockRepository, mockValidator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.addNote("Catatan Baru", "Isi Catatan Baru")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify {
            mockRepository.insertNote(
                match { it.title == "Catatan Baru" && it.content == "Isi Catatan Baru" }
            )
        }
    }

    @Test
    fun `addNote tidak memanggil repository jika validasi gagal`() = runTest {
        // Arrange
        coEvery { mockRepository.getAllNotes() } returns flowOf(emptyList())
        every { mockValidator.validate(any()) } throws ValidationException("Judul tidak boleh kosong")

        val viewModel = NotesViewModel(mockRepository, mockValidator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.addNote("", "Isi tanpa judul")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: insertNote tidak dipanggil sama sekali
        coVerify(exactly = 0) { mockRepository.insertNote(any()) }
    }

    @Test
    fun `deleteNote memanggil repository deleteNote dengan id yang benar`() = runTest {
        // Arrange
        coEvery { mockRepository.deleteNote(any()) } just Runs
        val viewModel = NotesViewModel(mockRepository, mockValidator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteNote(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockRepository.deleteNote(1L) }
    }

    @Test
    fun `deleteAllNotes memanggil repository deleteAllNotes`() = runTest {
        // Arrange
        coEvery { mockRepository.deleteAllNotes() } just Runs
        val viewModel = NotesViewModel(mockRepository, mockValidator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockRepository.deleteAllNotes() }
    }

    @Test
    fun `updateSearchQuery memperbarui nilai searchQuery`() = runTest {
        // Arrange
        val viewModel = NotesViewModel(mockRepository, mockValidator)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.updateSearchQuery("beli")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("beli", viewModel.searchQuery.value)
    }
}
