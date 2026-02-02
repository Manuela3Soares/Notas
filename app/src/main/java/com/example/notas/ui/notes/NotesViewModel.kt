package com.example.notas.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notas.data.Note
import com.example.notas.domain.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _showArchived = MutableStateFlow(false)
    val showArchived = _showArchived.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<Note>> = combine(
        _searchQuery,
        _showArchived
    ) { query, archived ->
        Pair(query, archived)
    }.flatMapLatest { (query, archived) ->
        when {
            archived -> repository.getArchivedNotes()
            query.isNotBlank() -> repository.searchNotes(query)
            else -> repository.getAllActiveNotes()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleShowArchived() {
        _showArchived.value = !_showArchived.value
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repository.togglePin(note)
        }
    }

    fun archiveNote(note: Note) {
        viewModelScope.launch {
            repository.archiveNote(note)
        }
    }

    fun unarchiveNote(note: Note) {
        viewModelScope.launch {
            repository.unarchiveNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}