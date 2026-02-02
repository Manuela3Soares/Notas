package com.example.notas.ui.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notas.domain.NoteRepository
import com.example.notas.ui.editor.EditorScreen
import com.example.notas.ui.editor.EditorViewModel
import com.example.notas.ui.notes.NotesListScreen
import com.example.notas.ui.notes.NotesViewModel

@Composable
fun App(repository: NoteRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "notes_list") {
        composable("notes_list") {
            NotesListScreen(
                viewModel = NotesViewModel(repository),
                onNoteClick = { noteId ->
                    navController.navigate("editor/$noteId")
                },
                onCreateNote = {
                    navController.navigate("editor/new")
                }
            )
        }

        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteIdStr = backStackEntry.arguments?.getString("noteId")
            val noteId = if (noteIdStr == "new") null else noteIdStr?.toLongOrNull()

            EditorScreen(
                viewModel = EditorViewModel(repository, noteId),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}