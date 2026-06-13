package com.example.myprofile.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myprofile.data.model.Note
import com.example.myprofile.ui.components.TestTags

/**
 * Layar utama daftar catatan.
 * Semua elemen interaktif memiliki testTag untuk keperluan UI testing.
 */
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    NotesContent(
        uiState      = uiState,
        searchQuery  = searchQuery,
        onAddNote    = viewModel::addNote,
        onDeleteNote = viewModel::deleteNote,
        onSearch     = viewModel::updateSearchQuery,
        onDeleteAll  = viewModel::deleteAllNotes,
        modifier     = modifier
    )
}

@Composable
fun NotesContent(
    uiState      : NotesUiState,
    searchQuery  : String,
    onAddNote    : (String, String) -> Unit,
    onDeleteNote : (Long) -> Unit,
    onSearch     : (String) -> Unit,
    onDeleteAll  : () -> Unit,
    modifier     : Modifier = Modifier
) {
    var titleText   by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text  = "My Notes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value           = searchQuery,
            onValueChange   = onSearch,
            modifier        = Modifier.fillMaxWidth().testTag(TestTags.SEARCH_INPUT),
            placeholder     = { Text("Cari catatan...") },
            leadingIcon     = { Icon(Icons.Default.Search, contentDescription = "Cari") },
            singleLine      = true
        )

        Spacer(Modifier.height(12.dp))

        // Add note inputs
        OutlinedTextField(
            value         = titleText,
            onValueChange = { titleText = it },
            modifier      = Modifier.fillMaxWidth().testTag(TestTags.TITLE_INPUT),
            placeholder   = { Text("Judul catatan...") },
            singleLine    = true,
            label         = { Text("Judul") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value         = contentText,
            onValueChange = { contentText = it },
            modifier      = Modifier.fillMaxWidth().height(100.dp).testTag(TestTags.CONTENT_INPUT),
            placeholder   = { Text("Isi catatan...") },
            label         = { Text("Isi") }
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick  = {
                    if (titleText.isNotBlank()) {
                        onAddNote(titleText, contentText)
                        titleText   = ""
                        contentText = ""
                    }
                },
                modifier = Modifier.weight(1f).testTag(TestTags.ADD_BUTTON)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Tambah")
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(
                onClick  = onDeleteAll,
                modifier = Modifier.testTag(TestTags.CLEAR_ALL_BUTTON)
            ) {
                Text("Hapus Semua")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Content area
        when (uiState) {
            is NotesUiState.Loading -> {
                Box(
                    modifier          = Modifier.fillMaxWidth().testTag(TestTags.LOADING_INDICATOR),
                    contentAlignment  = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is NotesUiState.Error -> {
                Text(
                    text     = uiState.message,
                    color    = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(TestTags.ERROR_MESSAGE)
                )
            }

            is NotesUiState.Success -> {
                val filtered = if (uiState.searchQuery.isBlank()) uiState.notes
                               else uiState.notes.filter {
                                   it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                                   it.content.contains(uiState.searchQuery, ignoreCase = true)
                               }

                if (filtered.isEmpty()) {
                    Box(
                        modifier         = Modifier.fillMaxWidth().testTag(TestTags.EMPTY_STATE),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "Belum ada catatan. Tambahkan catatan pertama Anda!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier            = Modifier.fillMaxSize().testTag(TestTags.NOTES_LIST),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { note ->
                            NoteCard(
                                note     = note,
                                onDelete = { onDeleteNote(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note    : Note,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth().testTag(TestTags.NOTE_ITEM),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = note.title,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.testTag(TestTags.NOTE_TITLE)
                )
                if (note.content.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text     = note.content,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        modifier = Modifier.testTag(TestTags.NOTE_CONTENT)
                    )
                }
            }
            IconButton(
                onClick  = onDelete,
                modifier = Modifier.testTag(TestTags.DELETE_BUTTON)
            ) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Hapus catatan",
                    tint               = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
