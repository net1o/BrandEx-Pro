package com.example.data

import kotlinx.coroutines.flow.Flow

class BrandExRepository(private val dao: BrandExDao) {
    val allClients: Flow<List<ClientEntity>> = dao.getAllClients()
    val allCases: Flow<List<TrademarkCaseEntity>> = dao.getAllCases()
    val allDeadlines: Flow<List<DocketDeadlineEntity>> = dao.getAllDeadlines()
    val allTransactions: Flow<List<LedgerTransactionEntity>> = dao.getAllTransactions()
    val allTaxRecords: Flow<List<TaxNtnRecordEntity>> = dao.getAllTaxRecords()

    suspend fun insertClient(client: ClientEntity) = dao.insertClient(client)
    suspend fun deleteClient(clientId: Long) = dao.deleteClient(clientId)

    suspend fun insertCase(trademarkCase: TrademarkCaseEntity) = dao.insertCase(trademarkCase)
    suspend fun deleteCase(caseId: Long) = dao.deleteCase(caseId)

    suspend fun insertDeadline(deadline: DocketDeadlineEntity) = dao.insertDeadline(deadline)
    suspend fun updateDeadline(deadline: DocketDeadlineEntity) = dao.updateDeadline(deadline)
    suspend fun deleteDeadline(deadlineId: Long) = dao.deleteDeadline(deadlineId)

    suspend fun insertTransaction(transaction: LedgerTransactionEntity) = dao.insertTransaction(transaction)
    suspend fun deleteTransaction(transactionId: Long) = dao.deleteTransaction(transactionId)

    suspend fun insertTaxRecord(record: TaxNtnRecordEntity) = dao.insertTaxRecord(record)
    suspend fun deleteTaxRecord(recordId: Long) = dao.deleteTaxRecord(recordId)
}
