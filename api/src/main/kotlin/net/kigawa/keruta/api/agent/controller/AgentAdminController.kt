package net.kigawa.keruta.api.agent.controller

import net.kigawa.keruta.core.domain.model.Agent
import net.kigawa.keruta.core.domain.model.AgentStatus
import net.kigawa.keruta.core.usecase.agent.AgentService
import org.springframework.beans.factory.annotation.Autowired
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
@RequestMapping("/admin/agents")
class AgentAdminController @Autowired constructor(private val agentService: AgentService) {

    @GetMapping
    fun agentList(model: Model): String {
        model.addAttribute("pageTitle", "Agent Management")
        model.addAttribute("agents", agentService.getAllAgents())
        return "admin/agents"
    }

    @GetMapping("/create")
    fun createAgentForm(model: Model): String {
        model.addAttribute("pageTitle", "Create Agent")
        model.addAttribute("agent", Agent(
            name = "",
            languages = emptyList(),
            status = AgentStatus.AVAILABLE
        )
        )
        model.addAttribute("statuses", AgentStatus.entries.toTypedArray())
        return "admin/agent-form"
    }

    @GetMapping("/edit/{id}")
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

    @GetMapping("/delete/{id}")
    fun deleteAgent(@PathVariable id: String): String {
        try {
            agentService.deleteAgent(id)
        } catch (e: NoSuchElementException) {
            // Ignore if agent not found
        }
        return "redirect:/admin/agents"
    }

    @GetMapping("/view/{id}")
    fun viewAgent(@PathVariable id: String, model: Model): String {
        try {
            val agent = agentService.getAgentById(id)
            model.addAttribute("pageTitle", "Agent Details")
            model.addAttribute("agent", agent)
            return "admin/agent-details"
        } catch (e: NoSuchElementException) {
            return "redirect:/admin/agents"
        }
    }

    @PostMapping("/create")
    fun createAgent(@ModelAttribute agent: Agent): String {
        try {
            agentService.createAgent(agent)
        } catch (e: Exception) {
            // Log error and handle exception
            // For now, just redirect to the list page
        }
        return "redirect:/admin/agents"
    }

    @PostMapping("/edit/{id}")
    fun updateAgent(@PathVariable id: String, @ModelAttribute agent: Agent): String {
        try {
            agentService.updateAgent(id, agent)
        } catch (e: Exception) {
            // Log error and handle exception
            // For now, just redirect to the list page
        }
        return "redirect:/admin/agents"
    }
}
