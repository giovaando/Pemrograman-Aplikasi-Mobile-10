package com.example.myprofile.ui.notes

import com.example.myprofile.data.model.Note

/**
 * UI State untuk layar daftar catatan.
 * Menggunakan sealed class agar setiap state direpresentasikan secara eksplisit.
 */
sealed class NotesUiState {
    /** Sedang memuat data */
    object Loading : NotesUiState()

    /** Data berhasil dimuat */
    data class Success(
        val notes: List<Note>,
        val searchQuery: String = ""
    ) : NotesUiState()

    /** Terjadi error */
    data class Error(val message: String) : NotesUiState()
}
