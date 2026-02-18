package net.kigawa.keruta.ktcl.k8s.task

import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.k8s.JobTemplateLoader
import net.kigawa.keruta.ktcl.k8s.k8s.K8sClientFactory
import net.kigawa.keruta.ktcl.k8s.k8s.K8sJobExecutor
import net.kigawa.keruta.ktcl.k8s.k8s.K8sJobWatcher
import net.kigawa.keruta.ktcp.client.KtcpClient

class TaskExecutorFactory(
    private val config: K8sConfig,
    private val ktcpClient: KtcpClient,
) {
    fun create(): TaskExecutor {
        val k8sClient = K8sClientFactory.createClient(config)
        val templateLoader = JobTemplateLoader(config.k8sJobTemplate)
        val jobExecutor = K8sJobExecutor(k8sClient, config, templateLoader)
        val jobWatcher = K8sJobWatcher(k8sClient, config)
        return TaskExecutor(jobExecutor, jobWatcher, ktcpClient)
    }
}
