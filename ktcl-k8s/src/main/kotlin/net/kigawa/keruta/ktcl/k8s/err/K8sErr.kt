package net.kigawa.keruta.ktcl.k8s.err

import net.kigawa.keruta.ktcp.domain.err.KtcpErr

sealed class K8sErr(message: String, cause: Throwable?) : KtcpErr(message, cause as? Exception) {
    abstract override val code: String

    class JobCreateErr(message: String, cause: Throwable?) : K8sErr(message, cause) {
        override val code = "JOB_CREATE_ERR"
    }

    class JobWatchErr(message: String, cause: Throwable?) : K8sErr(message, cause) {
        override val code = "JOB_WATCH_ERR"
    }

}
