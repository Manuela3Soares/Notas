package com.example.notas.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notas.data.Note
import com.example.notas.domain.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    private val repository: NoteRepository,
    private val noteId: Long?,
    private val userId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(EditorState())
    val state = _state.asStateFlow()

    init {
        noteId?.let { loadNote(it) }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val note = repository.getNoteById(id)
                if (note != null) {
                    _state.value = EditorState(
                        note = note,
                        title = note.title,
                        content = note.content,
                        label = note.label ?: "",
                        pinned = note.pinned,
                        archived = note.archived,
                        remindAt = note.remindAt,
                        colorInt = note.colorInt
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title, error = null)
    }

    fun updateContent(content: String) {
        _state.value = _state.value.copy(content = content)
    }

    fun updateLabel(label: String) {
        _state.value = _state.value.copy(label = label)
    }

    fun togglePinned() {
        _state.value = _state.value.copy(pinned = !_state.value.pinned)
    }

    fun toggleArchived() {
        _state.value = _state.value.copy(archived = !_state.value.archived)
    }

    fun updateColor(color: Int?) {
        _state.value = _state.value.copy(colorInt = color)
    }


    suspend fun saveNote(): Result<Unit> {
        val currentState = _state.value

        if (currentState.title.isBlank()) {
            _state.value = currentState.copy(error = "Título é obrigatório")
            return Result.failure(Exception("Título é obrigatório"))
        }

        return try {
            val note = currentState.note?.copy(
                title = currentState.title,
                content = currentState.content,
                label = currentState.label.ifBlank { null },
                pinned = currentState.pinned,
                archived = currentState.archived,
                remindAt = currentState.remindAt,
                colorInt = currentState.colorInt,
                updatedAt = System.currentTimeMillis()
            ) ?: Note(
                userId = userId, // Assign current user
                title = currentState.title,
                content = currentState.content,
                label = currentState.label.ifBlank { null },
                pinned = currentState.pinned,
                archived = currentState.archived,
                remindAt = currentState.remindAt,
                colorInt = currentState.colorInt
            )

            if (note.id == 0L) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            _state.value = currentState.copy(error = e.message)
            Result.failure(e)
        }
    }

    suspend fun deleteNote(): Result<Unit> {
        val note = _state.value.note ?: return Result.failure(Exception("Nota não encontrada"))
        return try {
            repository.deleteNote(note)
            Result.success(Unit)
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = e.message)
            Result.failure(e)
        }
    }
}

/*class EditorViewModelFactory(
    private val repository: NoteRepository,
    private val noteId: Long?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditorViewModel(repository, noteId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/



data class EditorState(
    val note: Note? = null,
    val title: String = "",
    val content: String = "",
    val label: String = "",
    val pinned: Boolean = false,
    val archived: Boolean = false,
    val remindAt: Long? = null,
    val colorInt: Int? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)