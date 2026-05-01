package net.kigawa.keruta.ktcl.mobile.task

import kotlin.test.Test
import kotlin.test.assertEquals
import net.kigawa.keruta.ktcl.mobile.msg.task.Task

class TaskRepositoryTest {

    @Test
    fun testUpdateTasks() {
        val repository = TaskRepository()
        
        val tasks = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1", status = "pending"),
            Task(id = 2, title = "Task 2", description = "Description 2", status = "completed")
        )
        
        repository.updateTasks(tasks)
        
        assertEquals(2, repository.tasks.value.size)
        assertEquals("Task 1", repository.tasks.value[0].title)
        assertEquals("Task 2", repository.tasks.value[1].title)
    }

    @Test
    fun testAddTask() {
        val repository = TaskRepository()
        
        val task1 = Task(id = 1, title = "Task 1", description = "Description 1", status = "pending")
        val task2 = Task(id = 2, title = "Task 2", description = "Description 2", status = "completed")
        
        repository.addTask(task1)
        repository.addTask(task2)
        
        assertEquals(2, repository.tasks.value.size)
    }

    @Test
    fun testUpdateTask() {
        val repository = TaskRepository()
        
        val task = Task(id = 1, title = "Task 1", description = "Description 1", status = "pending")
        repository.updateTasks(listOf(task))
        
        repository.updateTask(1) { it.copy(status = "completed") }
        
        assertEquals("completed", repository.tasks.value[0].status)
    }

    @Test
    fun testRemoveTask() {
        val repository = TaskRepository()
        
        val tasks = listOf(
            Task(id = 1, title = "Task 1", description = "Description 1", status = "pending"),
            Task(id = 2, title = "Task 2", description = "Description 2", status = "completed")
        )
        repository.updateTasks(tasks)
        
        repository.removeTask(1)
        
        assertEquals(1, repository.tasks.value.size)
        assertEquals(2, repository.tasks.value[0].id)
    }

    @Test
    fun testUpdateNonExistentTask() {
        val repository = TaskRepository()
        
        val task = Task(id = 1, title = "Task 1", description = "Description 1", status = "pending")
        repository.updateTasks(listOf(task))
        
        // 更新対象のタスクが存在しない場合は何もしない
        repository.updateTask(999) { it.copy(status = "completed") }
        
        assertEquals("pending", repository.tasks.value[0].status)
    }
}
