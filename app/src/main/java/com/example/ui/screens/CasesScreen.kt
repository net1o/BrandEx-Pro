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
import com.example.data.ClientEntity
import com.example.data.TrademarkCaseEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasesScreen(
    cases: List<TrademarkCaseEntity>,
    clients: List<ClientEntity>,
    onAddCase: (Long, String, String, String, Int, String, String, String) -> Unit,
    onDeleteCase: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCases = cases.filter {
        it.trademarkName.contains(searchQuery, ignoreCase = true) ||
        it.applicationNo.contains(searchQuery, ignoreCase = true) ||
        it.clientName.contains(searchQuery, ignoreCase = true) ||
        it.status.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_case_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Trademark Case")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Trademark Cases",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by trademark, application #, client") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("case_search_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredCases.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trademark cases found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("case_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCases) { c ->
                        CaseCard(trademarkCase = c, onDelete = { onDeleteCase(c.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCaseDialog(
            clients = clients,
            onDismiss = { showAddDialog = false },
            onConfirm = { clientId, clientName, trademark, appNo, classNo, filingDate, status, desc ->
                onAddCase(clientId, clientName, trademark, appNo, classNo, filingDate, status, desc)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CaseCard(trademarkCase: TrademarkCaseEntity, onDelete: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("case_card_${trademarkCase.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = trademarkCase.trademarkName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = trademarkCase.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_case_${trademarkCase.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Case",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(
                        text = "App: ${trademarkCase.applicationNo}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                    Text(
                        text = "Class ${trademarkCase.classNumber}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Badge(
                    containerColor = when (trademarkCase.status) {
                        "Registered" -> MaterialTheme.colorScheme.primaryContainer
                        "Opposed" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(
                        text = trademarkCase.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = trademarkCase.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Filing Date: ${trademarkCase.filingDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaseDialog(
    clients: List<ClientEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, String, Int, String, String, String) -> Unit
) {
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var expandedClientDropdown by remember { mutableStateOf(false) }
    var trademark by remember { mutableStateOf("") }
    var appNo by remember { mutableStateOf("") }
    var classNo by remember { mutableStateOf("9") }
    var filingDate by remember { mutableStateOf("2026-03-01") }
    var status by remember { mutableStateOf("Filed") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Trademark Case") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Client selector dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedClientDropdown,
                    onExpandedChange = { expandedClientDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedClient?.name ?: "Select Client",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClientDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedClientDropdown,
                        onDismissRequest = { expandedClientDropdown = false }
                    ) {
                        clients.forEach { client ->
                            DropdownMenuItem(
                                text = { Text(client.name) },
                                onClick = {
                                    selectedClient = client
                                    expandedClientDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = trademark,
                    onValueChange = { trademark = it },
                    label = { Text("Trademark Name") },
                    modifier = Modifier.fillMaxWidth().testTag("input_trademark_name"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = appNo,
                    onValueChange = { appNo = it },
                    label = { Text("Application Number (e.g. TM/2026/...)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_app_no"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = classNo,
                    onValueChange = { classNo = it },
                    label = { Text("Nice Class Number (1-45)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_class_no"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description & Specification") },
                    modifier = Modifier.fillMaxWidth().testTag("input_case_desc")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedClient != null && trademark.isNotBlank()) {
                        onConfirm(
                            selectedClient!!.id,
                            selectedClient!!.name,
                            trademark,
                            appNo,
                            classNo.toIntOrNull() ?: 9,
                            filingDate,
                            status,
                            desc
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_add_case")
            ) {
                Text("Add Case")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
