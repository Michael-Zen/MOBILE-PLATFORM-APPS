package com.example.ch07.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import com.example.ch07.util.formatTags
import com.example.ch07.util.parseTags
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteEditUiState(
    val title: String = "",
    val content: String = "",
    val tagsText: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class NoteEditViewModel(
    private val repository: NoteRepository,
    private val noteId: Int = 0,
    private val clock: () -> Long = System::currentTimeMillis
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditUiState(isLoading = noteId != 0))
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    private var original: NoteEntity? = null

    init {
        if (noteId != 0) loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            original = note
            _uiState.update {
                if (note == null) {
                    it.copy(isLoading = false, errorMessage = "Catatan tidak ditemukan")
                } else {
                    it.copy(
                        isLoading = false,
                        title = note.title,
                        content = note.content,
                        tagsText = formatTags(note.tags)
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, errorMessage = null) }

    fun onContentChange(value: String) =
        _uiState.update { it.copy(content = value) }

    fun onTagsChange(value: String) =
        _uiState.update { it.copy(tagsText = value) }

    fun save() {
        val current = _uiState.value
        if (current.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Judul tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val now = clock()
            val tags = parseTags(current.tagsText)
            val existing = original

            if (existing == null) {
                repository.insertNote(
                    NoteEntity(
                        title = current.title.trim(),
                        content = current.content.trim(),
                        createdAt = now,
                        isPinned = false,
                        tags = tags,
                        updatedAt = now
                    )
                )
            } else {
                repository.updateNote(
                    existing.copy(
                        title = current.title.trim(),
                        content = current.content.trim(),
                        tags = tags,
                        updatedAt = now
                    )
                )
            }

            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
