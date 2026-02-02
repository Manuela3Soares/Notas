package com.example.notas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE archived = 0 ORDER BY pinned DESC, updatedAt DESC")
    fun getAllActiveNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE archived = 1 ORDER BY updatedAt DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Query("SELECT * FROM notes WHERE archived = 0 AND (title LIKE '%' || :query || '%' OR label LIKE '%' || :query || '%') ORDER BY pinned DESC, updatedAt DESC")
    fun searchNotes(query: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
