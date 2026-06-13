package com.example.myprofile.data

import com.example.myprofile.data.model.Note
import com.example.myprofile.util.NoteValidator
import com.example.myprofile.util.ValidationException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

/**
 * Unit test untuk NoteValidator.
 * Menguji semua skenario validasi menggunakan pola AAA (Arrange-Act-Assert).
 */
class NoteValidatorTest {

    private lateinit var validator: NoteValidator

    @BeforeTest
    fun setup() {
        validator = NoteValidator()
    }

    @Test
    fun `catatan valid mengembalikan true`() {
        val note = Note(title = "Belanja", content = "Beli susu dan telur")
        val result = validator.isValid(note)
        assertTrue(result, "Catatan dengan judul dan konten yang valid harus diterima")
    }

    @Test
    fun `catatan dengan konten kosong tetap valid jika judul ada`() {
        val note = Note(title = "Judul Saja", content = "")
        val result = validator.isValid(note)
        assertTrue(result)
    }

    @Test
    fun `catatan dengan judul kosong mengembalikan false`() {
        val note = Note(title = "", content = "Ada isi tapi tidak ada judul")
        val result = validator.isValid(note)
        assertFalse(result, "Catatan tanpa judul harus ditolak")
    }

    @Test
    fun `catatan dengan judul hanya spasi mengembalikan false`() {
        val note = Note(title = "   ", content = "Isi catatan")
        val result = validator.isValid(note)
        assertFalse(result)
    }

    @Test
    fun `judul melebihi panjang maksimum mengembalikan false`() {
        val longTitle = "a".repeat(NoteValidator.MAX_TITLE_LENGTH + 1)
        val note = Note(title = longTitle, content = "Isi")
        val result = validator.isValid(note)
        assertFalse(result)
    }

    @Test
    fun `konten melebihi panjang maksimum mengembalikan false`() {
        val longContent = "x".repeat(NoteValidator.MAX_CONTENT_LENGTH + 1)
        val note = Note(title = "Judul", content = longContent)
        val result = validator.isValid(note)
        assertFalse(result)
    }

    @Test
    fun `validate dengan judul kosong melempar ValidationException`() {
        val note = Note(title = "", content = "Isi catatan")
        assertFailsWith<ValidationException> {
            validator.validate(note)
        }
    }

    @Test
    fun `validate dengan judul terlalu panjang melempar ValidationException`() {
        val longTitle = "a".repeat(NoteValidator.MAX_TITLE_LENGTH + 1)
        val note = Note(title = longTitle, content = "")
        assertFailsWith<ValidationException> {
            validator.validate(note)
        }
    }

    @Test
    fun `validate dengan input valid tidak melempar exception`() {
        val note = Note(title = "Judul Valid", content = "Isi yang valid")
        validator.validate(note) // Should not throw
    }
}
