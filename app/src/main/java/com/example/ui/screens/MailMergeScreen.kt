package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun MailMergeScreen(
    clients: List<ClientEntity>,
    cases: List<TrademarkCaseEntity>
) {
    var selectedTemplate by remember { mutableStateOf("TM-1 (Application for Registration)") }
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var selectedCase by remember { mutableStateOf(cases.firstOrNull()) }
    var generatedPreview by remember { mutableStateOf("") }
    var expandedClient by remember { mutableStateOf(false) }
    var expandedCase by remember { mutableStateOf(false) }
    var expandedTemplate by remember { mutableStateOf(false) }

    val templates = listOf(
        "TM-1 (Application for Registration)",
        "TM-48 (Form of Authorization of Agent)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Document & Mail-Merge Generator",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        ExposedDropdownMenuBox(
            expanded = expandedTemplate,
            onExpandedChange = { expandedTemplate = it }
        ) {
            OutlinedTextField(
                value = selectedTemplate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Document Template") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTemplate) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedTemplate,
                onDismissRequest = { expandedTemplate = false }
            ) {
                templates.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = {
                            selectedTemplate = t
                            expandedTemplate = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expandedClient,
            onExpandedChange = { expandedClient = it }
        ) {
            OutlinedTextField(
                value = selectedClient?.name ?: "Select Client",
                onValueChange = {},
                readOnly = true,
                label = { Text("Client") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClient) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedClient,
                onDismissRequest = { expandedClient = false }
            ) {
                clients.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.name) },
                        onClick = {
                            selectedClient = c
                            expandedClient = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expandedCase,
            onExpandedChange = { expandedCase = it }
        ) {
            OutlinedTextField(
                value = selectedCase?.let { "${it.trademarkName} (${it.applicationNo})" } ?: "Select Trademark Case",
                onValueChange = {},
                readOnly = true,
                label = { Text("Trademark Case") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCase) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedCase,
                onDismissRequest = { expandedCase = false }
            ) {
                cases.forEach { cs ->
                    DropdownMenuItem(
                        text = { Text("${cs.trademarkName} - ${cs.applicationNo}") },
                        onClick = {
                            selectedCase = cs
                            expandedCase = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val client = selectedClient
                val cCase = selectedCase
                generatedPreview = if (selectedTemplate.contains("TM-1")) {
                    """
                    TRADE MARKS REGISTRY
                    FORM TM-1: APPLICATION FOR REGISTRATION OF TRADEMARK
                    
                    Applicant Name: ${client?.name ?: "N/A"}
                    Address: ${client?.address ?: "N/A"}
                    NTN: ${client?.ntnNumber ?: "N/A"}
                    
                    Trademark: ${cCase?.trademarkName ?: "N/A"}
                    Application Number: ${cCase?.applicationNo ?: "N/A"}
                    Nice Classification: Class ${cCase?.classNumber ?: 9}
                    Specification: ${cCase?.description ?: "N/A"}
                    Filing Date: ${cCase?.filingDate ?: "N/A"}
                    
                    Declaration: The applicant claims to be the proprietor of the trademark.
                    """.trimIndent()
                } else {
                    """
                    TRADE MARKS REGISTRY
                    FORM TM-48: FORM OF AUTHORIZATION OF AGENT
                    
                    I/We, ${client?.name ?: "N/A"} having address at ${client?.address ?: "N/A"} do hereby appoint BrandEx Legal Chambers as our authorized patent and trademark attorneys to act on our behalf in respect of trademark "${cCase?.trademarkName ?: "N/A"}" (Application No: ${cCase?.applicationNo ?: "N/A"}).
                    
                    Signature: ______________________
                    Date: ${cCase?.filingDate ?: "N/A"}
                    """.trimIndent()
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("generate_doc_btn")
        ) {
            Icon(Icons.Default.Description, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Document Preview")
        }

        if (generatedPreview.isNotBlank()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("doc_preview_card")
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = "Generated Output Preview",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = generatedPreview,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
