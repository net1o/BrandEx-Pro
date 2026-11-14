package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.DocketDeadlineEntity
import com.example.data.TrademarkCaseEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocketScreen(
    deadlines: List<DocketDeadlineEntity>,
    cases: List<TrademarkCaseEntity>,
    onAddDeadline: (Long, String, String, String, String) -> Unit,
    onToggleCompletion: (DocketDeadlineEntity) -> Unit,
    onDeleteDeadline: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var filterCompleted by remember { mutableStateOf(false) }

    val displayedDeadlines = deadlines.filter { if (filterCompleted) true else !it.isCompleted }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_deadline_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Deadline")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IP Docket & Deadlines",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                FilterChip(
                    selected = filterCompleted,
                    onClick = { filterCompleted = !filterCompleted },
                    label = { Text(if (filterCompleted) "Show Pending" else "Show All") },
                    leadingIcon = {
                        if (filterCompleted) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("filter_completed_chip")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (displayedDeadlines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No deadlines found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("docket_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedDeadlines) { deadline ->
                        DeadlineCard(
                            deadline = deadline,
                            onToggle = { onToggleCompletion(deadline) },
                            onDelete = { onDeleteDeadline(deadline.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDeadlineDialog(
            cases = cases,
            onDismiss = { showAddDialog = false },
            onConfirm = { caseId, trademark, title, dueDate, priority ->
                onAddDeadline(caseId, trademark, title, dueDate, priority)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DeadlineCard(
    deadline: DocketDeadlineEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deadline_card_${deadline.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = deadline.isCompleted,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("checkbox_deadline_${deadline.id}")
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deadline.trademarkName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = deadline.title,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Due: ${deadline.dueDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (deadline.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                    )
                    Badge(
                        containerColor = if (deadline.priority == "High") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(text = deadline.priority, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_deadline_${deadline.id}")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeadlineDialog(
    cases: List<TrademarkCaseEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, String, String) -> Unit
) {
    var selectedCase by remember { mutableStateOf(cases.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-06-30") }
    var priority by remember { mutableStateOf("High") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Statutory Deadline") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCase?.let { "${it.trademarkName} (${it.applicationNo})" } ?: "Select Case",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        cases.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.trademarkName} - ${c.applicationNo}") },
                                onClick = {
                                    selectedCase = c
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Deadline Title (e.g. Opposition Reply)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_deadline_title"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_deadline_date"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCase != null && title.isNotBlank()) {
                        onConfirm(
                            selectedCase!!.id,
                            selectedCase!!.trademarkName,
                            title,
                            dueDate,
                            priority
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_add_deadline")
            ) {
                Text("Add Deadline")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
