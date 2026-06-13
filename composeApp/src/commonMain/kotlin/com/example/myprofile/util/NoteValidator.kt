package com.example.myprofile.util

import com.example.myprofile.data.model.Note

/**
 * Validator untuk memastikan data Note valid sebelum disimpan.
 */
class NoteValidator {

    companion object {
        const val MAX_TITLE_LENGTH = 200
        const val MAX_CONTENT_LENGTH = 5000
    }

    /**
     * Memeriksa apakah catatan valid (tidak throw exception).
     * @return true jika valid, false jika tidak
     */
    fun isValid(note: Note): Boolean {
        return note.title.isNotBlank() &&
               note.title.length <= MAX_TITLE_LENGTH &&
               note.content.length <= MAX_CONTENT_LENGTH
    }

    /**
     * Memvalidasi catatan dan melempar [ValidationException] jika tidak valid.
     */
    fun validate(note: Note) {
        when {
            note.title.isBlank() ->
                throw ValidationException("Judul catatan tidak boleh kosong.")
            note.title.length > MAX_TITLE_LENGTH ->
                throw ValidationException("Judul catatan terlalu panjang (maks $MAX_TITLE_LENGTH karakter).")
            note.content.length > MAX_CONTENT_LENGTH ->
                throw ValidationException("Isi catatan terlalu panjang (maks $MAX_CONTENT_LENGTH karakter).")
        }
    }
}

class ValidationException(message: String) : Exception(message)
