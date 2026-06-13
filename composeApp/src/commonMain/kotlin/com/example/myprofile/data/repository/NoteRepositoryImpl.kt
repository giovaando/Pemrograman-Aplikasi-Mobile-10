package com.example.myprofile.data.repository

import com.example.myprofile.data.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Implementasi NoteRepository berbasis in-memory.
 * Menggunakan MutableStateFlow agar perubahan data langsung
 * terpancar ke semua subscriber (Flow-based reactivity).
 */
class NoteRepositoryImpl : NoteRepository {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L

    override fun getAllNotes(): Flow<List<Note>> =
        _notes.map { it.sortedByDescending { n -> n.updatedAt } }

    override fun searchNotes(query: String): Flow<List<Note>> =
        _notes.map { notes ->
            if (query.isBlank()) notes
            else notes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true)
            }
        }

    override suspend fun getNoteById(id: Long): Note? =
        _notes.value.find { it.id == id }

    override suspend fun insertNote(note: Note): Long {
        val now = System.currentTimeMillis()
        val newNote = note.copy(
            id = nextId++,
            createdAt = now,
            updatedAt = now
        )
        _notes.update { current -> current + newNote }
        return newNote.id
    }

    override suspend fun updateNote(note: Note) {
        val now = System.currentTimeMillis()
        val updated = note.copy(updatedAt = now)
        _notes.update { current ->
            current.map { if (it.id == note.id) updated else it }
        }
    }

    override suspend fun deleteNote(id: Long) {
        _notes.update { current -> current.filter { it.id != id } }
    }

    override suspend fun deleteAllNotes() {
        _notes.update { emptyList() }
    }
}
