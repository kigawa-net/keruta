package net.kigawa.keruta.api.agent.controller

import net.kigawa.keruta.core.domain.model.Agent
import net.kigawa.keruta.core.domain.model.AgentStatus
import net.kigawa.keruta.core.usecase.agent.AgentService
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
class ExtendedAgentAdminController(private val agentService: AgentService) : AgentAdminController(agentService) {

    @PostMapping("/create")
    fun createAgentSubmit(@ModelAttribute agent: Agent): String {
        try {
            agentService.createAgent(agent)
        } catch (e: Exception) {
            // Log error and handle exception
            // For now, just redirect to the list page
        }
        return "redirect:/admin/agents"
    }

    @PostMapping("/edit/{id}")
    fun updateAgentSubmit(@PathVariable id: String, @ModelAttribute agent: Agent): String {
        try {
            agentService.updateAgent(id, agent)
        } catch (e: Exception) {
            // Log error and handle exception
            // For now, just redirect to the list page
        }
        return "redirect:/admin/agents"
    }
}