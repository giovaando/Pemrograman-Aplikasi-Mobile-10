package com.example.myprofile.data.model

import kotlinx.serialization.Serializable

/**
 * Model data untuk sebuah catatan.
 */
@Serializable
data class Note(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
