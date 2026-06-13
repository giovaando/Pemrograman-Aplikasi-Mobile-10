package com.example.myprofile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.myprofile.ui.notes.NotesScreen
import com.example.myprofile.ui.notes.NotesViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

/**
 * Entry point utama aplikasi.
 * KoinContext menyediakan DI context untuk seluruh composable tree.
 */
@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            Surface {
                val viewModel: NotesViewModel = koinViewModel()
                NotesScreen(viewModel = viewModel)
            }
        }
    }
}
