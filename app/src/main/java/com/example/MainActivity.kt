package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.BrandExTheme
import com.example.ui.viewmodel.BrandExViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrandExTheme {
                BrandExApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Clients : Screen("clients", "Clients", Icons.Default.Business)
    object Cases : Screen("cases", "Cases", Icons.Default.FolderOpen)
    object Docket : Screen("docket", "Docket", Icons.Default.Alarm)
    object Ledger : Screen("ledger", "Ledger", Icons.Default.AccountBalanceWallet)
    object TaxVault : Screen("tax", "Tax & NTN", Icons.Default.Security)
    object MailMerge : Screen("docs", "Docs", Icons.Default.Description)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandExApp(viewModel: BrandExViewModel = viewModel()) {
    var currentRoute by remember { mutableStateOf("dashboard") }

    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val cases by viewModel.cases.collectAsStateWithLifecycle()
    val deadlines by viewModel.deadlines.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val taxRecords by viewModel.taxRecords.collectAsStateWithLifecycle()

    val screens = listOf(
        Screen.Dashboard,
        Screen.Clients,
        Screen.Cases,
        Screen.Docket,
        Screen.Ledger,
        Screen.TaxVault,
        Screen.MailMerge
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("BrandEx Pro - Trademark & Legal Workstation") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation")
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = { currentRoute = screen.route },
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen(
                    clients = clients,
                    cases = cases,
                    deadlines = deadlines,
                    transactions = transactions,
                    taxRecords = taxRecords,
                    onNavigateTab = { currentRoute = it }
                )
                Screen.Clients.route -> ClientsScreen(
                    clients = clients,
                    onAddClient = { name, contact, email, phone, ntn, address ->
                        viewModel.addClient(name, contact, email, phone, ntn, address)
                    },
                    onDeleteClient = { id -> viewModel.deleteClient(id) }
                )
                Screen.Cases.route -> CasesScreen(
                    cases = cases,
                    clients = clients,
                    onAddCase = { clientId, clientName, trademark, appNo, cls, filingDate, status, desc ->
                        viewModel.addCase(clientId, clientName, trademark, appNo, cls, filingDate, status, desc)
                    },
                    onDeleteCase = { id -> viewModel.deleteCase(id) }
                )
                Screen.Docket.route -> DocketScreen(
                    deadlines = deadlines,
                    cases = cases,
                    onAddDeadline = { caseId, trademark, title, dueDate, priority ->
                        viewModel.addDeadline(caseId, trademark, title, dueDate, priority)
                    },
                    onToggleCompletion = { deadline -> viewModel.toggleDeadlineCompletion(deadline) },
                    onDeleteDeadline = { id -> viewModel.deleteDeadline(id) }
                )
                Screen.Ledger.route -> LedgerScreen(
                    transactions = transactions,
                    clients = clients,
                    onAddTransaction = { clientId, clientName, type, desc, amount, date, status ->
                        viewModel.addTransaction(clientId, clientName, type, desc, amount, date, status)
                    },
                    onDeleteTransaction = { id -> viewModel.deleteTransaction(id) }
                )
                Screen.TaxVault.route -> TaxVaultScreen(
                    taxRecords = taxRecords,
                    onAddTaxRecord = { name, ntn, year, status, date, notes ->
                        viewModel.addTaxRecord(name, ntn, year, status, date, notes)
                    },
                    onDeleteTaxRecord = { id -> viewModel.deleteTaxRecord(id) }
                )
                Screen.MailMerge.route -> MailMergeScreen(
                    clients = clients,
                    cases = cases
                )
            }
        }
    }
}
