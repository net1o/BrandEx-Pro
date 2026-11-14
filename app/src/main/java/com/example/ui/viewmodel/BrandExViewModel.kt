package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BrandExDatabase
import com.example.data.BrandExRepository
import com.example.data.ClientEntity
import com.example.data.DocketDeadlineEntity
import com.example.data.LedgerTransactionEntity
import com.example.data.TaxNtnRecordEntity
import com.example.data.TrademarkCaseEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrandExViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BrandExRepository

    init {
        val dao = BrandExDatabase.getDatabase(application).brandExDao()
        repository = BrandExRepository(dao)
    }

    val clients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cases: StateFlow<List<TrademarkCaseEntity>> = repository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deadlines: StateFlow<List<DocketDeadlineEntity>> = repository.allDeadlines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<LedgerTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taxRecords: StateFlow<List<TaxNtnRecordEntity>> = repository.allTaxRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addClient(name: String, contact: String, email: String, phone: String, ntn: String, address: String) {
        viewModelScope.launch {
            repository.insertClient(
                ClientEntity(
                    name = name,
                    contactPerson = contact,
                    email = email,
                    phone = phone,
                    ntnNumber = ntn,
                    address = address
                )
            )
        }
    }

    fun deleteClient(id: Long) {
        viewModelScope.launch { repository.deleteClient(id) }
    }

    fun addCase(clientId: Long, clientName: String, trademark: String, appNo: String, cls: Int, filingDate: String, status: String, desc: String) {
        viewModelScope.launch {
            repository.insertCase(
                TrademarkCaseEntity(
                    clientId = clientId,
                    clientName = clientName,
                    trademarkName = trademark,
                    applicationNo = appNo,
                    classNumber = cls,
                    filingDate = filingDate,
                    status = status,
                    description = desc
                )
            )
        }
    }

    fun deleteCase(id: Long) {
        viewModelScope.launch { repository.deleteCase(id) }
    }

    fun addDeadline(caseId: Long, trademark: String, title: String, dueDate: String, priority: String) {
        viewModelScope.launch {
            repository.insertDeadline(
                DocketDeadlineEntity(
                    caseId = caseId,
                    trademarkName = trademark,
                    title = title,
                    dueDate = dueDate,
                    priority = priority
                )
            )
        }
    }

    fun toggleDeadlineCompletion(deadline: DocketDeadlineEntity) {
        viewModelScope.launch {
            repository.updateDeadline(deadline.copy(isCompleted = !deadline.isCompleted))
        }
    }

    fun deleteDeadline(id: Long) {
        viewModelScope.launch { repository.deleteDeadline(id) }
    }

    fun addTransaction(clientId: Long, clientName: String, type: String, desc: String, amount: Double, date: String, status: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                LedgerTransactionEntity(
                    clientId = clientId,
                    clientName = clientName,
                    type = type,
                    description = desc,
                    amount = amount,
                    date = date,
                    status = status
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch { repository.deleteTransaction(id) }
    }

    fun addTaxRecord(name: String, ntn: String, year: String, status: String, date: String, notes: String) {
        viewModelScope.launch {
            repository.insertTaxRecord(
                TaxNtnRecordEntity(
                    clientOrEntityName = name,
                    ntnNumber = ntn,
                    taxYear = year,
                    filingStatus = status,
                    lastFilingDate = date,
                    secureNotes = notes
                )
            )
        }
    }

    fun deleteTaxRecord(id: Long) {
        viewModelScope.launch { repository.deleteTaxRecord(id) }
    }
}
