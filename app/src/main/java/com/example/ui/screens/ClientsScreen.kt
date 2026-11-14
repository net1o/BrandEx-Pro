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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    clients: List<ClientEntity>,
    onAddClient: (String, String, String, String, String, String) -> Unit,
    onDeleteClient: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredClients = clients.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.contactPerson.contains(searchQuery, ignoreCase = true) ||
        it.ntnNumber.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_client_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Client")
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
                text = "Client Records",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search clients by name or NTN") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_search_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredClients.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No clients found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("client_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredClients) { client ->
                        ClientCard(client = client, onDelete = { onDeleteClient(client.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddClientDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, contact, email, phone, ntn, address ->
                onAddClient(name, contact, email, phone, ntn, address)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ClientCard(client: ClientEntity, onDelete: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("client_card_${client.id}")
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
                    text = client.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_client_${client.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Client",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Contact: ${client.contactPerson} | ${client.phone}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Email: ${client.email}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        text = client.ntnNumber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    text = client.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddClientDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var ntn by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Client") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Company / Client Name") },
                    modifier = Modifier.fillMaxWidth().testTag("input_client_name"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Person") },
                    modifier = Modifier.fillMaxWidth().testTag("input_client_contact"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().testTag("input_client_email"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth().testTag("input_client_phone"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ntn,
                    onValueChange = { ntn = it },
                    label = { Text("NTN Number") },
                    modifier = Modifier.fillMaxWidth().testTag("input_client_ntn"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth().testTag("input_client_address"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, contact, email, phone, ntn, address)
                    }
                },
                modifier = Modifier.testTag("confirm_add_client")
            ) {
                Text("Add Client")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
