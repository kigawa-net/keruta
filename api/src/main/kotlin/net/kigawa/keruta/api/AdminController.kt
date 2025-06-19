package net.kigawa.keruta.api

import net.kigawa.keruta.core.domain.model.Agent
import net.kigawa.keruta.core.domain.model.AgentStatus
import net.kigawa.keruta.core.domain.model.Document
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.agent.AgentService
import net.kigawa.keruta.core.usecase.document.DocumentService
import net.kigawa.keruta.core.usecase.task.TaskService
import net.kigawa.keruta.core.usecase.repository.GitRepositoryService
import net.kigawa.keruta.core.usecase.repository.TaskRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime
import java.util.UUID

@Controller
@RequestMapping("/admin")
class AdminController(
    private val taskRepository: TaskRepository,
    private val agentService: AgentService,
    private val documentService: DocumentService,
    private val gitRepositoryService: GitRepositoryService,
    private val taskService: TaskService
) {

    @GetMapping
    fun adminDashboard(model: Model): String {
        model.addAttribute("pageTitle", "Admin Dashboard")
        model.addAttribute("tasks", taskRepository.findAll())
        model.addAttribute("agents", agentService.getAllAgents())
        return "admin/dashboard"
    }

    @GetMapping("/tasks")
    fun taskList(model: Model): String {
        model.addAttribute("pageTitle", "Task Management")
        model.addAttribute("tasks", taskRepository.findAll())
        return "admin/tasks"
    }

    @GetMapping("/tasks/create")
    fun createTaskForm(model: Model): String {
        model.addAttribute("pageTitle", "Create Task")
        model.addAttribute("task", Task(
            title = "",
            description = null,
            priority = 0,
            status = TaskStatus.PENDING
        ))
        model.addAttribute("statuses", TaskStatus.entries.toTypedArray())
        model.addAttribute("documents", documentService.getAllDocuments())
        model.addAttribute("repositories", gitRepositoryService.getAllRepositories())
        return "admin/task-form"
    }

    @PostMapping("/tasks/create")
    fun createTask(
        @ModelAttribute task: Task,
        @RequestParam(required = false) repositoryId: String?,
        @RequestParam(required = false) documentIds: List<String>?
    ): String {
        val repository = if (repositoryId != null) gitRepositoryService.getRepositoryById(repositoryId) else null
        val documents = documentIds?.mapNotNull { documentService.getDocumentById(it) } ?: emptyList()

        // Ensure status is not null
        val status = task.status ?: TaskStatus.PENDING

        // Ensure priority is not null
        val priority = task.priority

        // Ensure title is not empty or null
        val title = if (task.title.isNullOrBlank()) "Untitled Task" else task.title

        val newTask = task.copy(
            id = UUID.randomUUID().toString(),
            title = title,
            description = task.description,
            priority = priority,
            status = status,
            repository = repository,
            documents = documents,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        try {
            taskService.createTask(newTask)
        } catch (e: Exception) {
            // Log the error
            println("Error creating task: ${e.message}")
            // Fall back to direct repository save if service fails
            taskRepository.save(newTask)
        }
        return "redirect:/admin/tasks"
    }

    @GetMapping("/tasks/edit/{id}")
    fun editTaskForm(@PathVariable id: String, model: Model): String {
        val task = taskRepository.findById(id)
        if (task != null) {
            model.addAttribute("pageTitle", "Edit Task")
            model.addAttribute("task", task)
            model.addAttribute("statuses", TaskStatus.entries.toTypedArray())
            model.addAttribute("documents", documentService.getAllDocuments())
            model.addAttribute("repositories", gitRepositoryService.getAllRepositories())
            return "admin/task-form"
        }
        return "redirect:/admin/tasks"
    }

    @PostMapping("/tasks/edit/{id}")
    fun updateTask(
        @PathVariable id: String,
        @ModelAttribute task: Task,
        @RequestParam(required = false) repositoryId: String?,
        @RequestParam(required = false) documentIds: List<String>?
    ): String {
        if (taskRepository.findById(id) != null) {
            val repository = if (repositoryId != null) gitRepositoryService.getRepositoryById(repositoryId) else null
            val documents = documentIds?.mapNotNull { documentService.getDocumentById(it) } ?: emptyList()

            // Ensure status is not null
            val status = task.status ?: TaskStatus.PENDING

            // Ensure priority is not null
            val priority = task.priority

            // Ensure title is not empty or null
            val title = if (task.title.isNullOrBlank()) "Untitled Task" else task.title

            val updatedTask = task.copy(
                id = id,
                title = title,
                description = task.description,
                priority = priority,
                status = status,
                repository = repository,
                documents = documents,
                updatedAt = LocalDateTime.now()
            )
            try {
                taskService.updateTask(id, updatedTask)
            } catch (e: Exception) {
                // Log the error
                println("Error updating task: ${e.message}")
                // Fall back to direct repository save if service fails
                taskRepository.save(updatedTask)
            }
        }
        return "redirect:/admin/tasks"
    }

    @GetMapping("/tasks/delete/{id}")
    fun deleteTask(@PathVariable id: String): String {
        try {
            taskService.deleteTask(id)
        } catch (e: Exception) {
            // Log the error
            println("Error deleting task: ${e.message}")
            // Fall back to direct repository delete if service fails
            taskRepository.deleteById(id)
        }
        return "redirect:/admin/tasks"
    }

    @GetMapping("/tasks/logs/{id}")
    fun viewTaskLogs(@PathVariable id: String, model: Model): String {
        try {
            val task = taskService.getTaskById(id)
            model.addAttribute("pageTitle", "Task Logs")
            model.addAttribute("task", task)
            return "admin/task-logs"
        } catch (e: NoSuchElementException) {
            return "redirect:/admin/tasks"
        }
    }

    // Agent Management

    @GetMapping("/agents")
    fun agentList(model: Model): String {
        model.addAttribute("pageTitle", "Agent Management")
        model.addAttribute("agents", agentService.getAllAgents())
        return "admin/agents"
    }

    @GetMapping("/agents/create")
    fun createAgentForm(model: Model): String {
        model.addAttribute("pageTitle", "Create Agent")
        model.addAttribute("agent", Agent(
            name = "",
            languages = emptyList(),
            status = AgentStatus.AVAILABLE
        ))
        model.addAttribute("statuses", AgentStatus.entries.toTypedArray())
        return "admin/agent-form"
    }

    @PostMapping("/agents/create")
    fun createAgent(@ModelAttribute agent: Agent, @RequestParam languagesInput: String): String {
        val languages = languagesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        val newAgent = agent.copy(
            name = agent.name,
            languages = languages,
            status = agent.status ?: AgentStatus.AVAILABLE
        )

        agentService.createAgent(newAgent)
        return "redirect:/admin/agents"
    }

    @GetMapping("/agents/edit/{id}")
    fun editAgentForm(@PathVariable id: String, model: Model): String {
        try {
            val agent = agentService.getAgentById(id)
            model.addAttribute("pageTitle", "Edit Agent")
            model.addAttribute("agent", agent)
            model.addAttribute("statuses", AgentStatus.entries.toTypedArray())
            return "admin/agent-form"
        } catch (e: NoSuchElementException) {
            return "redirect:/admin/agents"
        }
    }

    @PostMapping("/agents/edit/{id}")
    fun updateAgent(
        @PathVariable id: String,
        @ModelAttribute agent: Agent,
        @RequestParam languagesInput: String
    ): String {
        try {
            val languages = languagesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val updatedAgent = agent.copy(
                id = id,
                name = agent.name,
                languages = languages,
                status = agent.status ?: AgentStatus.AVAILABLE
            )

            agentService.updateAgent(id, updatedAgent)
            return "redirect:/admin/agents"
        } catch (e: NoSuchElementException) {
            return "redirect:/admin/agents"
        }
    }

    @GetMapping("/agents/delete/{id}")
    fun deleteAgent(@PathVariable id: String): String {
        try {
            agentService.deleteAgent(id)
        } catch (e: NoSuchElementException) {
            // Ignore if agent doesn't exist
        }
        return "redirect:/admin/agents"
    }
}
