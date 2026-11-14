package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandExDao {

    // Clients
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClient(clientId: Long)

    // Trademark Cases
    @Query("SELECT * FROM trademark_cases ORDER BY filingDate DESC")
    fun getAllCases(): Flow<List<TrademarkCaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(trademarkCase: TrademarkCaseEntity): Long

    @Query("DELETE FROM trademark_cases WHERE id = :caseId")
    suspend fun deleteCase(caseId: Long)

    // Docket Deadlines
    @Query("SELECT * FROM docket_deadlines ORDER BY dueDate ASC")
    fun getAllDeadlines(): Flow<List<DocketDeadlineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: DocketDeadlineEntity): Long

    @Update
    suspend fun updateDeadline(deadline: DocketDeadlineEntity)

    @Query("DELETE FROM docket_deadlines WHERE id = :deadlineId")
    suspend fun deleteDeadline(deadlineId: Long)

    // Ledger Transactions
    @Query("SELECT * FROM ledger_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<LedgerTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: LedgerTransactionEntity): Long

    @Query("DELETE FROM ledger_transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)

    // Tax & NTN Records
    @Query("SELECT * FROM tax_ntn_records ORDER BY taxYear DESC")
    fun getAllTaxRecords(): Flow<List<TaxNtnRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxRecord(record: TaxNtnRecordEntity): Long

    @Query("DELETE FROM tax_ntn_records WHERE id = :recordId")
    suspend fun deleteTaxRecord(recordId: Long)
}
