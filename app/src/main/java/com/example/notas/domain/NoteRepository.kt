package com.example.notas.domain

import com.example.notas.data.Note
import com.example.notas.data.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllActiveNotes(userId: Long): Flow<List<Note>> = noteDao.getAllActiveNotes(userId)

    fun getArchivedNotes(userId: Long): Flow<List<Note>> = noteDao.getArchivedNotes(userId)

    fun searchNotes(userId: Long, query: String): Flow<List<Note>> = noteDao.searchNotes(userId, query)

    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note): Long {
        validateNote(note)
        return noteDao.insert(note)
    }

    suspend fun updateNote(note: Note) {
        validateNote(note)
        noteDao.update(note.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    suspend fun togglePin(note: Note) {
        noteDao.update(note.copy(pinned = !note.pinned, updatedAt = System.currentTimeMillis()))
    }

    suspend fun archiveNote(note: Note) {
        noteDao.update(note.copy(archived = true, updatedAt = System.currentTimeMillis()))
    }

    suspend fun unarchiveNote(note: Note) {
        noteDao.update(note.copy(archived = false, updatedAt = System.currentTimeMillis()))
    }

    private fun validateNote(note: Note) {
        require(note.title.isNotBlank()) { "Título é obrigatório" }

        note.remindAt?.let { remindTime ->
            val minFutureTime = System.currentTimeMillis() + 60_000 // +1 minuto
            require(remindTime >= minFutureTime) {
                "Lembrete deve ser no futuro (mínimo 1 minuto)"
            }
        }
    }
}