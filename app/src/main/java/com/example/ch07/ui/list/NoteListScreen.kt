package com.example.ch07.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ch07.data.NoteEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    uiState: NoteListUiState,
    onQueryChange: (String) -> Unit,
    onAddNote: () -> Unit,
    onEditNote: (Int) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onUndoDelete: () -> Unit,
    onUndoDismissed: () -> Unit,
    onTagSelected: (String?) -> Unit,
    onTogglePin: (NoteEntity) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnUndoDelete by rememberUpdatedState(onUndoDelete)
    val currentOnUndoDismissed by rememberUpdatedState(onUndoDismissed)

    LaunchedEffect(uiState.recentlyDeleted) {
        if (uiState.recentlyDeleted != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Catatan dihapus",
                actionLabel = "Batalkan"
            )
            if (result == SnackbarResult.ActionPerformed) {
                currentOnUndoDelete()
            } else {
                currentOnUndoDismissed()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { TopAppBar(title = { Text("CatatanKu") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah catatan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            NoteSearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.availableTags.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedTag == null,
                            onClick = { onTagSelected(null) },
                            label = { Text("Semua") }
                        )
                    }
                    items(uiState.availableTags) { tag ->
                        FilterChip(
                            selected = uiState.selectedTag == tag,
                            onClick = { onTagSelected(tag) },
                            label = { Text("#$tag") }
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                uiState.errorMessage != null -> CenteredMessage(uiState.errorMessage)

                uiState.notes.isEmpty() -> CenteredMessage(emptyMessage(uiState))

                else -> {
                    val now = remember(uiState.notes) { System.currentTimeMillis() }
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.notes, key = { it.id }) { note ->
                            NoteCard(
                                now = now,
                                note = note,
                                onClick = { onEditNote(note.id) },
                                onDelete = { onDeleteNote(note) },
                                onTogglePin = { onTogglePin(note) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun emptyMessage(uiState: NoteListUiState): String = when {
    uiState.selectedTag != null ->
        "Tidak ada catatan dengan tag #${uiState.selectedTag}"
    uiState.query.isNotBlank() ->
        "Tidak ada catatan yang cocok dengan \"${uiState.query}\""
    else ->
        "Belum ada catatan. Ketuk + untuk memulai."
}

@Composable
private fun CenteredMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun NoteSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Cari judul atau isi catatan...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Hapus pencarian")
                }
            }
        },
        singleLine = true
    )
}
