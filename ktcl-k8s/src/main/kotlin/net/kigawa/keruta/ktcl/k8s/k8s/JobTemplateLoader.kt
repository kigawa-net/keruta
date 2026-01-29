package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.openapi.models.V1EnvVar
import io.kubernetes.client.openapi.models.V1Job
import io.kubernetes.client.util.Yaml
import java.io.File
import java.io.FileReader

class JobTemplateLoader(private val templatePath: String) {
    fun loadTemplate(taskId: Long, title: String, description: String): V1Job {
        // 1. YAMLファイルを読み込み
        val templateFile = File(templatePath)
        val reader = FileReader(templateFile)

        // 2. Kubernetes Java ClientのYaml.load()でV1Jobオブジェクトに変換
        val job = Yaml.load(reader) as V1Job

        // 3. Job名を動的生成
        job.metadata?.name("keruta-task-$taskId")

        // 4. 環境変数を設定
        val container = job.spec?.template?.spec?.containers?.get(0)
        container?.env(
            listOf(
                V1EnvVar().name("TASK_ID").value(taskId.toString()),
                V1EnvVar().name("TASK_TITLE").value(title),
                V1EnvVar().name("TASK_DESCRIPTION").value(description)
            )
        )

        return job
    }
}
