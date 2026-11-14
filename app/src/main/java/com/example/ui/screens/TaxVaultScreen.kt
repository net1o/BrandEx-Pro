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
import com.example.data.TaxNtnRecordEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxVaultScreen(
    taxRecords: List<TaxNtnRecordEntity>,
    onAddTaxRecord: (String, String, String, String, String, String) -> Unit,
    onDeleteTaxRecord: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_tax_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tax Record")
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
                    text = "Tax & NTN Vault",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.Security,
                    contentDescription = "Secure Vault",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (taxRecords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tax records in vault.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("tax_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(taxRecords) { record ->
                        TaxCard(record = record, onDelete = { onDeleteTaxRecord(record.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTaxDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, ntn, year, status, date, notes ->
                onAddTaxRecord(name, ntn, year, status, date, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TaxCard(record: TaxNtnRecordEntity, onDelete: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tax_card_${record.id}")
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
                Text(
                    text = record.clientOrEntityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_tax_${record.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(text = record.ntnNumber, style = MaterialTheme.typography.labelSmall)
                }
                Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                    Text(text = "TY ${record.taxYear}", style = MaterialTheme.typography.labelSmall)
                }
                Badge(
                    containerColor = if (record.filingStatus == "Filed") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(text = record.filingStatus, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = record.secureNotes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Last Filed: ${record.lastFilingDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddTaxDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var ntn by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2025-2026") }
    var status by remember { mutableStateOf("Filed") }
    var date by remember { mutableStateOf("2026-02-28") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tax & NTN Record") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Entity Name") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tax_name"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ntn,
                    onValueChange = { ntn = it },
                    label = { Text("NTN Number") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tax_ntn"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Tax Year") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tax_year"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Secure Filing Notes & Audit Status") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tax_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, ntn, year, status, date, notes)
                    }
                },
                modifier = Modifier.testTag("confirm_add_tax")
            ) {
                Text("Save Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
