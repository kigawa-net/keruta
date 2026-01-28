package net.kigawa.keruta.ktse.persist.db.dsl

import org.jetbrains.exposed.sql.Transaction

class DbPersisterDsl(val transaction: Transaction) {
    val task by lazy { DbTaskPersisterDsl(transaction) }
    val user by lazy { DbUserPersisterDsl(transaction) }
    val provider by lazy { DbProviderPersisterDsl(transaction) }
    val queue by lazy { DbQueuePersisterDsl(transaction) }
}
