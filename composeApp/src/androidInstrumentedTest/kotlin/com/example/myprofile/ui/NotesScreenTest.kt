package com.example.myprofile.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.myprofile.data.repository.NoteRepository
import com.example.myprofile.data.repository.NoteRepositoryImpl
import com.example.myprofile.ui.components.TestTags
import com.example.myprofile.ui.notes.NotesContent
import com.example.myprofile.ui.notes.NotesUiState
import com.example.myprofile.data.model.Note
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

/**
 * UI Test menggunakan Compose Test.
 * Menggunakan Test Tags agar test tidak bergantung pada teks UI.
 * Minimal 3 test cases sesuai rubrik penilaian.
 */
class NotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_menampilkan_pesan_kosong() {
        // Arrange: tampilkan UI dengan state kosong
        composeTestRule.setContent {
            MaterialTheme {
                NotesContent(
                    uiState      = NotesUiState.Success(notes = emptyList()),
                    searchQuery  = "",
                    onAddNote    = { _, _ -> },
                    onDeleteNote = {},
                    onSearch     = {},
                    onDeleteAll  = {}
                )
            }
        }

        // Assert: pesan empty state harus tampil
        composeTestRule
            .onNodeWithTag(TestTags.EMPTY_STATE)
            .assertIsDisplayed()
    }

    @Test
    fun daftar_catatan_ditampilkan_saat_ada_data() {
        // Arrange
        val notes = listOf(
            Note(id = 1L, title = "Catatan Pertama", content = "Isi pertama"),
            Note(id = 2L, title = "Catatan Kedua", content = "Isi kedua")
        )

        composeTestRule.setContent {
            MaterialTheme {
                NotesContent(
                    uiState      = NotesUiState.Success(notes = notes),
                    searchQuery  = "",
                    onAddNote    = { _, _ -> },
                    onDeleteNote = {},
                    onSearch     = {},
                    onDeleteAll  = {}
                )
            }
        }

        // Assert: kedua catatan harus tampil
        composeTestRule.onNodeWithText("Catatan Pertama").assertIsDisplayed()
        composeTestRule.onNodeWithText("Catatan Kedua").assertIsDisplayed()
    }

    @Test
    fun loading_indicator_ditampilkan_saat_state_loading() {
        // Arrange
        composeTestRule.setContent {
            MaterialTheme {
                NotesContent(
                    uiState      = NotesUiState.Loading,
                    searchQuery  = "",
                    onAddNote    = { _, _ -> },
                    onDeleteNote = {},
                    onSearch     = {},
                    onDeleteAll  = {}
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithTag(TestTags.LOADING_INDICATOR)
            .assertIsDisplayed()
    }

    @Test
    fun error_message_ditampilkan_saat_state_error() {
        // Arrange
        val errorMessage = "Terjadi kesalahan koneksi database"

        composeTestRule.setContent {
            MaterialTheme {
                NotesContent(
                    uiState      = NotesUiState.Error(errorMessage),
                    searchQuery  = "",
                    onAddNote    = { _, _ -> },
                    onDeleteNote = {},
                    onSearch     = {},
                    onDeleteAll  = {}
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithTag(TestTags.ERROR_MESSAGE)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun input_judul_dapat_menerima_teks()  {
        // Arrange
        composeTestRule.setContent {
            MaterialTheme {
                NotesContent(
                    uiState      = NotesUiState.Success(notes = emptyList()),
                    searchQuery  = "",
                    onAddNote    = { _, _ -> },
                    onDeleteNote = {},
                    onSearch     = {},
                    onDeleteAll  = {}
                )
            }
        }

        // Act: ketik teks pada field judul
        composeTestRule
            .onNodeWithTag(TestTags.TITLE_INPUT)
            .performTextInput("Judul Test")

        // Assert: teks harus tampil di field
        composeTestRule.onNodeWithText("Judul Test").assertIsDisplayed()
    }

    @Test
    fun tombol_tambah_ada_dan_bisa_diklik() {
        // Arrange
        var addCalled = false

        composeTestRule.setContent {
            MaterialTheme {
                NotesContent(
                    uiState      = NotesUiState.Success(notes = emptyList()),
                    searchQuery  = "",
                    onAddNote    = { _, _ -> addCalled = true },
                    onDeleteNote = {},
                    onSearch     = {},
                    onDeleteAll  = {}
                )
            }
        }

        // Isi judul agar bisa diklik
        composeTestRule
            .onNodeWithTag(TestTags.TITLE_INPUT)
            .performTextInput("Tes")

        // Act
        composeTestRule
            .onNodeWithTag(TestTags.ADD_BUTTON)
            .performClick()

        // Assert: callback harus dipanggil
        assert(addCalled) { "onAddNote harus dipanggil saat tombol Tambah diklik" }
    }

    @Test
    fun search_bar_ada_dan_dapat_menerima_input() {
        // Arrange
        composeTestRule.setContent {
            MaterialTheme {
                NotesContent(
                    uiState      = NotesUiState.Success(notes = emptyList()),
                    searchQuery  = "",
                    onAddNote    = { _, _ -> },
                    onDeleteNote = {},
                    onSearch     = {},
                    onDeleteAll  = {}
                )
            }
        }

        // Assert: search bar tampil
        composeTestRule
            .onNodeWithTag(TestTags.SEARCH_INPUT)
            .assertIsDisplayed()

        // Act: ketik di search bar
        composeTestRule
            .onNodeWithTag(TestTags.SEARCH_INPUT)
            .performTextInput("belanja")

        // Assert: search bar masih tampil setelah input
        composeTestRule
            .onNodeWithTag(TestTags.SEARCH_INPUT)
            .assertIsDisplayed()
    }
}
