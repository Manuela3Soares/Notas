package com.example.notas.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.error) {
        state.error?.let {
            // Show error via SnackBar if necessary
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.note != null) "Editar Nota" else "Nova Nota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (state.note != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Apagar")
                        }
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                val result = viewModel.saveNote()
                                if (result.isSuccess) {
                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.error != null && state.title.isBlank(),
                supportingText = {
                    if (state.error != null && state.title.isBlank()) {
                        Text("Título é obrigatório")
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                )
            )

            OutlinedTextField(
                value = state.label,
                onValueChange = { viewModel.updateLabel(it) },
                label = { Text("Etiqueta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                )
            )

            OutlinedTextField(
                value = state.content,
                onValueChange = { viewModel.updateContent(it) },
                label = { Text("Conteúdo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 5,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.pinned,
                    onClick = { viewModel.togglePinned() },
                    label = { Text("Fixar") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                FilterChip(
                    selected = state.archived,
                    onClick = { viewModel.toggleArchived() },
                    label = { Text("Arquivar") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Archive,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Apagar nota?") },
            text = { Text("Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deleteNote()
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("Apagar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
