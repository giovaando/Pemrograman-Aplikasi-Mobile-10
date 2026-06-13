package com.example.myprofile.ui.components

/**
 * Konstanta Test Tags untuk Compose UI Testing.
 * Menggunakan test tags memberikan stabilitas yang lebih baik
 * dibanding mencari node berdasarkan teks (rentan perubahan UI/bahasa).
 */
object TestTags {
    const val NOTES_LIST       = "notes_list"
    const val NOTE_ITEM        = "note_item"
    const val TITLE_INPUT      = "title_input"
    const val CONTENT_INPUT    = "content_input"
    const val ADD_BUTTON       = "add_button"
    const val DELETE_BUTTON    = "delete_button"
    const val SEARCH_INPUT     = "search_input"
    const val EMPTY_STATE      = "empty_state"
    const val LOADING_INDICATOR= "loading_indicator"
    const val ERROR_MESSAGE    = "error_message"
    const val NOTE_TITLE       = "note_title"
    const val NOTE_CONTENT     = "note_content"
    const val CLEAR_ALL_BUTTON = "clear_all_button"
}
