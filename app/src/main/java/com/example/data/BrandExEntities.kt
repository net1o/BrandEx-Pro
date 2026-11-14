package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val contactPerson: String,
    val email: String,
    val phone: String,
    val ntnNumber: String,
    val address: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trademark_cases")
data class TrademarkCaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientId: Long,
    val clientName: String,
    val trademarkName: String,
    val applicationNo: String,
    val classNumber: Int,
    val filingDate: String,
    val status: String, // "Filed", "Examined", "Published", "Opposed", "Registered", "Renewal Due"
    val description: String
)

@Entity(tableName = "docket_deadlines")
data class DocketDeadlineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val caseId: Long,
    val trademarkName: String,
    val title: String,
    val dueDate: String,
    val isCompleted: Boolean = false,
    val priority: String // "High", "Medium", "Low"
)

@Entity(tableName = "ledger_transactions")
data class LedgerTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientId: Long,
    val clientName: String,
    val type: String, // "Charge", "Receipt", "Receiving"
    val description: String,
    val amount: Double,
    val date: String,
    val status: String // "Paid", "Pending", "Void"
)

@Entity(tableName = "tax_ntn_records")
data class TaxNtnRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientOrEntityName: String,
    val ntnNumber: String,
    val taxYear: String,
    val filingStatus: String, // "Filed", "Audit Pending", "Draft"
    val lastFilingDate: String,
    val secureNotes: String
)
