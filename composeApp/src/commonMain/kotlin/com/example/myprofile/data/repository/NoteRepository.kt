package com.example.myprofile.data.repository

import com.example.myprofile.data.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Interface repository untuk operasi CRUD catatan.
 * Penggunaan interface memudahkan mocking saat unit testing.
 */
interface NoteRepository {
    /** Stream semua catatan, diupdate secara reaktif */
    fun getAllNotes(): Flow<List<Note>>

    /** Stream catatan berdasarkan query pencarian */
    fun searchNotes(query: String): Flow<List<Note>>

    /** Ambil satu catatan berdasarkan id */
    suspend fun getNoteById(id: Long): Note?

    /** Simpan catatan baru atau update catatan yang ada */
    suspend fun insertNote(note: Note): Long

    /** Update catatan yang sudah ada */
    suspend fun updateNote(note: Note)

    /** Hapus catatan berdasarkan id */
    suspend fun deleteNote(id: Long)

    /** Hapus semua catatan */
    suspend fun deleteAllNotes()
}
