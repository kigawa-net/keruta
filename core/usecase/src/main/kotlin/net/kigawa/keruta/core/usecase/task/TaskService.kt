/**
 * Service interface for Task operations.
 * This interface combines the previous TaskService and JobService interfaces.
 */
package net.kigawa.keruta.core.usecase.task

import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus

interface TaskService {
    /**
     * Gets all tasks.
     *
     * @return List of all tasks
     */
    fun getAllTasks(): List<Task>

    /**
     * Gets a task by its ID.
     *
     * @param id The ID of the task to get
     * @return The task if found
     * @throws NoSuchElementException if the task is not found
     */
    fun getTaskById(id: String): Task

    /**
     * Creates a new task.
     *
     * @param task The task to create
     * @return The created task with generated ID
     */
    fun createTask(task: Task): Task

    /**
     * Updates an existing task.
     *
     * @param id The ID of the task to update
     * @param task The updated task data
     * @return The updated task
     * @throws NoSuchElementException if the task is not found
     */
    fun updateTask(id: String, task: Task): Task

    /**
     * Deletes a task by its ID.
     *
     * @param id The ID of the task to delete
     * @throws NoSuchElementException if the task is not found
     */
    fun deleteTask(id: String)

    /**
     * Gets the next task from the queue.
     *
     * @return The next task in the queue, or null if the queue is empty
     */
    fun getNextTaskFromQueue(): Task?

    /**
     * Updates the status of a task.
     *
     * @param id The ID of the task to update
     * @param status The new status
     * @return The updated task
     * @throws NoSuchElementException if the task is not found
     */
    fun updateTaskStatus(id: String, status: TaskStatus): Task

    /**
     * Updates the priority of a task.
     *
     * @param id The ID of the task to update
     * @param priority The new priority
     * @return The updated task
     * @throws NoSuchElementException if the task is not found
     */
    fun updateTaskPriority(id: String, priority: Int): Task

    /**
     * Gets tasks by status.
     *
     * @param status The status to filter by
     * @return List of tasks with the specified status
     */
    fun getTasksByStatus(status: TaskStatus): List<Task>

    /**
     * Creates a Kubernetes pod for a task.
     *
     * @param taskId The ID of the task
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace (optional)
     * @param podName The name of the pod (optional)
     * @param resources The resource requirements (optional)
     * @param additionalEnv Additional environment variables (optional)
     * @return The updated task with pod information
     * @throws NoSuchElementException if the task is not found
     */
    fun createPod(
        taskId: String,
        image: String,
        namespace: String = "default",
        podName: String? = null,
        resources: Resources? = null,
        additionalEnv: Map<String, String> = emptyMap()
    ): Task

    /**
     * Appends logs to a task.
     *
     * @param id The ID of the task to update
     * @param logs The logs to append
     * @return The updated task
     * @throws NoSuchElementException if the task is not found
     */
    fun appendTaskLogs(id: String, logs: String): Task

    /**
     * Creates a pod automatically for the next task in the queue.
     *
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace (optional)
     * @param podName The name of the pod (optional)
     * @param resources The resource requirements (optional)
     * @param additionalEnv Additional environment variables (optional)
     * @return The updated task, or null if there are no tasks in the queue
     */
    fun createPodForNextTask(
        image: String,
        namespace: String = "default",
        podName: String? = null,
        resources: Resources? = null,
        additionalEnv: Map<String, String> = emptyMap()
    ): Task?

    /**
     * Sets the Kubernetes manifest for a task.
     *
     * @param id The ID of the task to update
     * @param manifest The Kubernetes manifest as a string
     * @return The updated task
     * @throws NoSuchElementException if the task is not found
     */
    fun setKubernetesManifest(id: String, manifest: String): Task
}
