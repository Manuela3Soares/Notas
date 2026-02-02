package com.example.notas.ui.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.notas.ui.notes.components.NoteCard
import com.example.notas.ui.notes.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Long) -> Unit,
    onCreateNote: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showArchived by viewModel.showArchived.collectAsState()

    Scaffold(
        topBar = {
            if (showArchived) {
                TopAppBar(
                    title = { Text("Arquivo") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.toggleShowArchived() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            } else {


               // Title and Floating Search Bar Header
               Column(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
               ) {
                   Text(
                       text = "Nota App",
                       style = MaterialTheme.typography.headlineLarge.copy(
                           fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                           color = MaterialTheme.colorScheme.onSurface
                       )
                   )
                   Spacer(modifier = Modifier.height(16.dp))
                   Box(
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       SearchBar(
                           query = searchQuery,
                           onQueryChange = { viewModel.updateSearchQuery(it) },
                           modifier = Modifier.fillMaxWidth()
                       )
                       
                       IconButton(
                           onClick = { viewModel.toggleShowArchived() },
                           modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                       ) {
                           Icon(Icons.Default.Archive, contentDescription = "Arquivo")
                       }
                   }
               }
            }
        },
        floatingActionButton = {
            if (!showArchived) {
                FloatingActionButton(
                    onClick = onCreateNote,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova nota")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (notes.isEmpty()) {
                 EmptyState(
                    message = if (showArchived)
                        "Nenhuma nota arquivada"
                    else if (searchQuery.isNotBlank())
                        "Nenhuma nota encontrada"
                    else
                        "Nenhuma nota criada",
                    onAction = if (!showArchived && searchQuery.isBlank()) onCreateNote else null
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(
                        items = notes,
                        key = { it.id }
                    ) { note ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(durationMillis = 500)
                            ) + fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.animateItemPlacement()
                        ) {
                            NoteCard(
                                note = note,
                                onClick = { onNoteClick(note.id) },
                                onTogglePin = { viewModel.togglePin(note) },
                                onArchive = {
                                    if (showArchived) {
                                        viewModel.unarchiveNote(note)
                                    } else {
                                        viewModel.archiveNote(note)
                                    }
                                },
                                onDelete = { viewModel.deleteNote(note) },
                                showArchived = showArchived
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyState(
    message: String,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Note,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
        if (onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAction) {
                Text("Criar Nova Nota")
            }
        }
    }
}
