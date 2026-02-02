package com.example.notas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notas.data.AppDatabase
import com.example.notas.domain.NoteRepository
import com.example.notas.ui.app.App
import com.example.notas.ui.theme.NotasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = NoteRepository(database.noteDao())

        setContent {
            NotasTheme {
                App(repository)
            }
        }
    }
}
