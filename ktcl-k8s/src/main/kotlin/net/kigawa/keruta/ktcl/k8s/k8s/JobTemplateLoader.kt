package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.openapi.models.V1EnvVar
import io.kubernetes.client.openapi.models.V1Job
import io.kubernetes.client.util.Yaml
import java.io.File
import java.io.FileReader

class JobTemplateLoader(private val templatePath: String) {
    fun loadTemplate(taskId: Long, title: String, description: String, gitRepoUrl: String): V1Job {
        val templateFile = File(templatePath)
        val reader = FileReader(templateFile)
        val job = Yaml.load(reader) as V1Job

        val jobName = "keruta-task-$taskId"
        job.metadata?.name(jobName)

        // PVCクレーム名をtaskIdベースに設定
        val pvcClaimName = "$jobName-pvc"
        job.spec?.template?.spec?.volumes
            ?.find { it.name == "workspace" }
            ?.persistentVolumeClaim?.claimName(pvcClaimName)

        val taskEnv = listOf(
            V1EnvVar().name("TASK_ID").value(taskId.toString()),
            V1EnvVar().name("TASK_TITLE").value(title),
            V1EnvVar().name("TASK_DESCRIPTION").value(description),
            V1EnvVar().name("GIT_REPO_URL").value(gitRepoUrl),
        )

        // git-clone initコンテナに環境変数を設定
        job.spec?.template?.spec?.initContainers
            ?.find { it.name == "git-clone" }
            ?.env(listOf(
                V1EnvVar().name("GIT_REPO_URL").value(gitRepoUrl),
                V1EnvVar().name("TASK_ID").value(taskId.toString()),
            ))

        // task-executor initコンテナに環境変数を設定
        job.spec?.template?.spec?.initContainers
            ?.find { it.name == "task-executor" }
            ?.env(taskEnv)

        // git-push メインコンテナにTASK_IDを設定
        job.spec?.template?.spec?.containers
            ?.find { it.name == "git-push" }
            ?.env(listOf(V1EnvVar().name("TASK_ID").value(taskId.toString())))

        return job
    }
}