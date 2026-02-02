package com.example.notas.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notas.domain.AuthRepository
import com.example.notas.domain.NoteRepository
import com.example.notas.ui.editor.EditorScreen
import com.example.notas.ui.editor.EditorViewModel
import com.example.notas.ui.notes.NotesListScreen
import com.example.notas.ui.notes.NotesViewModel

@Composable
fun App(
    repository: NoteRepository,
    authRepository: AuthRepository
) {
    val navController = rememberNavController()
    val currentUserId by authRepository.currentUserId.collectAsState(initial = null)

    LaunchedEffect(currentUserId) {
        if (currentUserId == null) {
            navController.navigate("login") {
                popUpTo(0)
            }
        } else {
            navController.navigate("notes_list") {
                popUpTo(0)
            }
        }
    }

    NavHost(navController = navController, startDestination = if (currentUserId != null) "notes_list" else "login") {
        composable("login") {
            com.example.notas.ui.auth.LoginScreen(
                viewModel = com.example.notas.ui.auth.AuthViewModel(authRepository),
                onLoginSuccess = { /* Handled by LaunchedEffect */ },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            com.example.notas.ui.auth.SignUpScreen(
                viewModel = com.example.notas.ui.auth.AuthViewModel(authRepository),
                onSignUpSuccess = { /* Handled by LaunchedEffect */ },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("notes_list") {
            currentUserId?.let { userId ->
                NotesListScreen(
                    viewModel = NotesViewModel(repository, userId, authRepository),
                    onNoteClick = { noteId ->
                        navController.navigate("editor/$noteId")
                    },
                    onCreateNote = {
                        navController.navigate("editor/new")
                    }
                )
            }
        }

        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteIdStr = backStackEntry.arguments?.getString("noteId")
            val noteId = if (noteIdStr == "new") null else noteIdStr?.toLongOrNull()

            currentUserId?.let { userId ->
                EditorScreen(
                    viewModel = EditorViewModel(repository, noteId, userId),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}