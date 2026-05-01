package net.kigawa.keruta.ktcl.mobile.task

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.kigawa.keruta.ktcl.mobile.msg.task.Task

class TaskRepository {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun updateTasks(newTasks: List<Task>) {
        _tasks.value = newTasks
    }

    fun addTask(task: Task) {
        _tasks.value = _tasks.value + task
    }

    fun updateTask(taskId: Long, updater: (Task) -> Task) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) updater(task) else task
        }
    }

    fun removeTask(taskId: Long) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }
}
