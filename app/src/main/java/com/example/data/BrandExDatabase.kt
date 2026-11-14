package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClientEntity::class,
        TrademarkCaseEntity::class,
        DocketDeadlineEntity::class,
        LedgerTransactionEntity::class,
        TaxNtnRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BrandExDatabase : RoomDatabase() {

    abstract fun brandExDao(): BrandExDao

    companion object {
        @Volatile
        private var INSTANCE: BrandExDatabase? = null

        fun getDatabase(context: Context): BrandExDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BrandExDatabase::class.java,
                    "brandex_database"
                )
                    .addCallback(BrandExDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class BrandExDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.brandExDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: BrandExDao) {
                val c1 = dao.insertClient(
                    ClientEntity(
                        name = "Apex Innovations Ltd",
                        contactPerson = "Sarah Jenkins",
                        email = "sarah@apexinnovations.com",
                        phone = "+1-555-0192",
                        ntnNumber = "NTN-884920-1",
                        address = "742 Evergreen Terrace, Suite 400"
                    )
                )
                val c2 = dao.insertClient(
                    ClientEntity(
                        name = "Vanguard Holdings Inc",
                        contactPerson = "Marcus Vance",
                        email = "mvance@vanguardholdings.org",
                        phone = "+1-555-8392",
                        ntnNumber = "NTN-993821-4",
                        address = "100 Financial Plaza, Floor 22"
                    )
                )
                val c3 = dao.insertClient(
                    ClientEntity(
                        name = "Quantum BioLabs",
                        contactPerson = "Dr. Elena Rostova",
                        email = "elena@quantumbio.io",
                        phone = "+1-555-4920",
                        ntnNumber = "NTN-449201-9",
                        address = "450 Science Parkway, Lab B"
                    )
                )

                val tc1 = dao.insertCase(
                    TrademarkCaseEntity(
                        clientId = c1,
                        clientName = "Apex Innovations Ltd",
                        trademarkName = "NEONPULSE",
                        applicationNo = "TM/2026/88412",
                        classNumber = 9,
                        filingDate = "2026-01-15",
                        status = "Published",
                        description = "Class 9: Software, AI Analytics, and Cloud Platforms"
                    )
                )
                val tc2 = dao.insertCase(
                    TrademarkCaseEntity(
                        clientId = c2,
                        clientName = "Vanguard Holdings Inc",
                        trademarkName = "VANGUARD SECURE",
                        applicationNo = "TM/2026/10492",
                        classNumber = 36,
                        filingDate = "2026-02-01",
                        status = "Examined",
                        description = "Class 36: Financial Services, Wealth Management & Escrow"
                    )
                )
                val tc3 = dao.insertCase(
                    TrademarkCaseEntity(
                        clientId = c3,
                        clientName = "Quantum BioLabs",
                        trademarkName = "GENOME-X",
                        applicationNo = "TM/2025/59201",
                        classNumber = 5,
                        filingDate = "2025-11-20",
                        status = "Opposed",
                        description = "Class 5: Pharmaceutical Preparations & Diagnostic Reagents"
                    )
                )

                dao.insertDeadline(
                    DocketDeadlineEntity(
                        caseId = tc1,
                        trademarkName = "NEONPULSE",
                        title = "Publication Opposition Window Closes",
                        dueDate = "2026-06-15",
                        priority = "High"
                    )
                )
                dao.insertDeadline(
                    DocketDeadlineEntity(
                        caseId = tc2,
                        trademarkName = "VANGUARD SECURE",
                        title = "Examination Report Response Due",
                        dueDate = "2026-07-10",
                        priority = "Medium"
                    )
                )
                dao.insertDeadline(
                    DocketDeadlineEntity(
                        caseId = tc3,
                        trademarkName = "GENOME-X",
                        title = "Counter-Statement Filing against Opposition",
                        dueDate = "2026-05-30",
                        priority = "High"
                    )
                )

                dao.insertTransaction(
                    LedgerTransactionEntity(
                        clientId = c1,
                        clientName = "Apex Innovations Ltd",
                        type = "Charge",
                        description = "Trademark Application Filing & Professional Fees",
                        amount = 1250.00,
                        date = "2026-01-15",
                        status = "Paid"
                    )
                )
                dao.insertTransaction(
                    LedgerTransactionEntity(
                        clientId = c2,
                        clientName = "Vanguard Holdings Inc",
                        type = "Charge",
                        description = "Class 36 Comprehensive Search & Opinion",
                        amount = 850.00,
                        date = "2026-02-01",
                        status = "Paid"
                    )
                )
                dao.insertTransaction(
                    LedgerTransactionEntity(
                        clientId = c3,
                        clientName = "Quantum BioLabs",
                        type = "Charge",
                        description = "Opposition Defense Retainer",
                        amount = 2200.00,
                        date = "2026-02-10",
                        status = "Pending"
                    )
                )

                dao.insertTaxRecord(
                    TaxNtnRecordEntity(
                        clientOrEntityName = "Apex Innovations Ltd",
                        ntnNumber = "NTN-884920-1",
                        taxYear = "2025-2026",
                        filingStatus = "Filed",
                        lastFilingDate = "2026-02-28",
                        secureNotes = "FBR Corporate Return Verified. Tax Clearance Certificate issued."
                    )
                )
                dao.insertTaxRecord(
                    TaxNtnRecordEntity(
                        clientOrEntityName = "Vanguard Holdings Inc",
                        ntnNumber = "NTN-993821-4",
                        taxYear = "2025-2026",
                        filingStatus = "Audit Pending",
                        lastFilingDate = "2026-01-15",
                        secureNotes = "Notice under Section 122 received. Response prepared by tax counsel."
                    )
                )
            }
        }
    }
}
