package com.example.myprofile.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myprofile.data.model.Note
import com.example.myprofile.data.repository.NoteRepository
import com.example.myprofile.util.NoteValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel untuk layar daftar catatan.
 * Dependencies di-inject melalui constructor (constructor injection).
 *
 * @param repository Repositori untuk operasi CRUD catatan
 * @param validator  Validator untuk memeriksa input catatan
 */
class NotesViewModel(
    private val repository: NoteRepository,
    private val validator: NoteValidator = NoteValidator()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadNotes()
    }

    /** Memuat semua catatan dari repository */
    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.value = NotesUiState.Loading
            repository.getAllNotes()
                .catch { e ->
                    _uiState.value = NotesUiState.Error(
                        e.message ?: "Terjadi kesalahan saat memuat catatan."
                    )
                }
                .collect { notes ->
                    _uiState.value = NotesUiState.Success(
                        notes = notes,
                        searchQuery = _searchQuery.value
                    )
                }
        }
    }

    /**
     * Menambahkan catatan baru.
     * Memvalidasi input sebelum menyimpan ke repository.
     */
    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            val note = Note(title = title.trim(), content = content.trim())
            try {
                validator.validate(note)
                repository.insertNote(note)
            } catch (e: Exception) {
                _uiState.value = NotesUiState.Error(e.message ?: "Input tidak valid.")
            }
        }
    }

    /**
     * Memperbarui catatan yang sudah ada.
     */
    fun updateNote(note: Note) {
        viewModelScope.launch {
            try {
                validator.validate(note)
                repository.updateNote(note)
            } catch (e: Exception) {
                _uiState.value = NotesUiState.Error(e.message ?: "Input tidak valid.")
            }
        }
    }

    /**
     * Menghapus catatan berdasarkan id.
     */
    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    /**
     * Menghapus semua catatan.
     */
    fun deleteAllNotes() {
        viewModelScope.launch {
            repository.deleteAllNotes()
        }
    }

    /**
     * Memperbarui query pencarian.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        val currentState = _uiState.value
        if (currentState is NotesUiState.Success) {
            _uiState.value = currentState.copy(searchQuery = query)
        }
    }
}
