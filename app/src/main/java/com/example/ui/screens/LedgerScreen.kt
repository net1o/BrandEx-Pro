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
import com.example.data.LedgerTransactionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    transactions: List<LedgerTransactionEntity>,
    clients: List<ClientEntity>,
    onAddTransaction: (Long, String, String, String, Double, String, String) -> Unit,
    onDeleteTransaction: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val totalPaid = transactions.filter { it.status == "Paid" }.sumOf { it.amount }
    val totalPending = transactions.filter { it.status == "Pending" }.sumOf { it.amount }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_transaction_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
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
                text = "Financial Ledger",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Realized Revenue", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "$${String.format("%,.2f", totalPaid)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Divider(modifier = Modifier.height(32.dp).width(1.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Pending Receivables", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "$${String.format("%,.2f", totalPending)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No financial transactions recorded.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("ledger_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions) { tx ->
                        TransactionCard(tx = tx, onDelete = { onDeleteTransaction(tx.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            clients = clients,
            onDismiss = { showAddDialog = false },
            onConfirm = { clientId, clientName, type, desc, amount, date, status ->
                onAddTransaction(clientId, clientName, type, desc, amount, date, status)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TransactionCard(tx: LedgerTransactionEntity, onDelete: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_card_${tx.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.clientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tx.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(text = tx.type, style = MaterialTheme.typography.labelSmall)
                    }
                    Badge(
                        containerColor = if (tx.status == "Paid") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(text = tx.status, style = MaterialTheme.typography.labelSmall)
                    }
                    Text(
                        text = tx.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%,.2f", tx.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (tx.status == "Paid") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_tx_${tx.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    clients: List<ClientEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, String, Double, String, String) -> Unit
) {
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("500.00") }
    var type by remember { mutableStateOf("Charge") }
    var status by remember { mutableStateOf("Pending") }
    var date by remember { mutableStateOf("2026-03-01") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Ledger Transaction") },
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
                        value = selectedClient?.name ?: "Select Client",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        clients.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.name) },
                                onClick = {
                                    selectedClient = c
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (e.g. Filing Fee)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tx_desc"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_tx_amount"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (selectedClient != null && amount > 0.0) {
                        onConfirm(
                            selectedClient!!.id,
                            selectedClient!!.name,
                            type,
                            description,
                            amount,
                            date,
                            status
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_add_tx")
            ) {
                Text("Add Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
