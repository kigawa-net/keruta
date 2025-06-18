package net.kigawa.keruta.api.kubernetes.controller

import net.kigawa.keruta.api.kubernetes.dto.KubernetesConfigDto
import net.kigawa.keruta.core.usecase.kubernetes.KubernetesService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/kubernetes")
class KubernetesAdminController(
    private val kubernetesService: KubernetesService,
    
    @Value("\${keruta.kubernetes.enabled:false}")
    private val kubernetesEnabled: Boolean,
    
    @Value("\${keruta.kubernetes.config-path:}")
    private val kubernetesConfigPath: String,
    
    @Value("\${keruta.kubernetes.in-cluster:false}")
    private val kubernetesInCluster: Boolean,
    
    @Value("\${keruta.kubernetes.default-namespace:default}")
    private val kubernetesDefaultNamespace: String,
    
    @Value("\${keruta.job.processor.default-image:keruta-task-executor:latest}")
    private val defaultImage: String,
    
    @Value("\${keruta.job.processor.default-namespace:default}")
    private val defaultNamespace: String
) {

    @GetMapping
    fun kubernetesSettings(model: Model): String {
        model.addAttribute("pageTitle", "Kubernetes Settings")
        
        val config = KubernetesConfigDto(
            enabled = kubernetesEnabled,
            configPath = kubernetesConfigPath,
            inCluster = kubernetesInCluster,
            defaultNamespace = kubernetesDefaultNamespace,
            defaultImage = defaultImage,
            processorNamespace = defaultNamespace
        )
        
        model.addAttribute("config", config)
        
        return "admin/kubernetes-settings"
    }
    
    @PostMapping("/update")
    fun updateKubernetesSettings(@ModelAttribute config: KubernetesConfigDto): String {
        // In a real implementation, this would update the configuration
        // For now, we'll just log the changes
        return "redirect:/admin/kubernetes"
    }
}