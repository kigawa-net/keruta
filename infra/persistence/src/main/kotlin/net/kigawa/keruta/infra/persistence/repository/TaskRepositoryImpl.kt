/**
 * Implementation of the TaskRepository interface using MongoDB.
 */
package net.kigawa.keruta.infra.persistence.repository

import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.repository.TaskRepository
import net.kigawa.keruta.infra.persistence.entity.TaskEntity
import org.springframework.stereotype.Component

@Component
class TaskRepositoryImpl(private val mongoTaskRepository: MongoTaskRepository) : TaskRepository {
    
    override fun findAll(): List<Task> {
        return mongoTaskRepository.findAll().map { it.toDomain() }
    }
    
    override fun findById(id: String): Task? {
        return mongoTaskRepository.findById(id).orElse(null)?.toDomain()
    }
    
    override fun save(task: Task): Task {
        val entity = TaskEntity.fromDomain(task)
        return mongoTaskRepository.save(entity).toDomain()
    }
    
    override fun deleteById(id: String): Boolean {
        return if (mongoTaskRepository.existsById(id)) {
            mongoTaskRepository.deleteById(id)
            true
        } else {
            false
        }
    }
    
    override fun findNextInQueue(): Task? {
        return mongoTaskRepository.findNextInQueue(TaskStatus.PENDING.name)?.toDomain()
    }
    
    override fun findByStatus(status: TaskStatus): List<Task> {
        return mongoTaskRepository.findByStatus(status.name).map { it.toDomain() }
    }
}