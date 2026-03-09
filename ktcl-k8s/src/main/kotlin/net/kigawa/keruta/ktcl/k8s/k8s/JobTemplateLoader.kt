package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.openapi.models.V1EnvVar
import io.kubernetes.client.openapi.models.V1Job
import io.kubernetes.client.util.Yaml
import java.io.InputStreamReader

class JobTemplateLoader(private val templatePath: String) {
    fun loadTemplate(taskId: Long, title: String, description: String, gitRepoUrl: String, githubToken: String): V1Job {
        val reader = InputStreamReader(
            JobTemplateLoader::class.java.classLoader.getResourceAsStream(templatePath)
                ?: error("Job template not found on classpath: $templatePath")
        )
        val job = Yaml.load(reader) as V1Job

        val jobName = "keruta-task-$taskId"
        job.metadata?.name(jobName)

        // PVCクレーム名をtaskIdベースに設定
        val pvcClaimName = "$jobName-pvc"
        job.spec?.template?.spec?.volumes
            ?.find { it.name == "workspace" }
            ?.persistentVolumeClaim?.claimName(pvcClaimName)

        val taskIdStr = taskId.toString()

        job.spec?.template?.spec?.initContainers
            ?.find { it.name == "git-clone" }
            ?.env(listOf(
                V1EnvVar().name("GIT_REPO_URL").value(gitRepoUrl),
                V1EnvVar().name("TASK_ID").value(taskIdStr),
            ))

        job.spec?.template?.spec?.initContainers
            ?.find { it.name == "task-executor" }
            ?.env(listOf(
                V1EnvVar().name("TASK_ID").value(taskIdStr),
                V1EnvVar().name("TASK_TITLE").value(title),
                V1EnvVar().name("TASK_DESCRIPTION").value(description),
                V1EnvVar().name("GIT_REPO_URL").value(gitRepoUrl),
            ))

        job.spec?.template?.spec?.initContainers
            ?.find { it.name == "git-push" }
            ?.env(listOf(V1EnvVar().name("TASK_ID").value(taskIdStr)))

        job.spec?.template?.spec?.containers
            ?.find { it.name == "github-pr" }
            ?.env(listOf(
                V1EnvVar().name("TASK_ID").value(taskIdStr),
                V1EnvVar().name("TASK_TITLE").value(title),
                V1EnvVar().name("TASK_DESCRIPTION").value(description),
                V1EnvVar().name("GIT_REPO_URL").value(gitRepoUrl),
                V1EnvVar().name("GITHUB_TOKEN").value(githubToken),
            ))

        return job
    }
}