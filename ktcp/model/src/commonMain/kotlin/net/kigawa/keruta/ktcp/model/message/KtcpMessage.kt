 package net.kigawa.keruta.ktcp.model.message

// KTCP メッセージタイプの列挙型
enum class KtcpMessageType(val value: String) {
    AUTHENTICATE("authenticate"),
    HEARTBEAT("heartbeat"),
    TASK_CREATE("task_create"),
    TASK_CREATE_RESPONSE("task_create_response"),
    TASK_READ("task_read"),
    TASK_READ_RESPONSE("task_read_response"),
    TASK_UPDATE("task_update"),
    TASK_UPDATE_RESPONSE("task_update_response"),
    TASK_DELETE("task_delete"),
    TASK_DELETE_RESPONSE("task_delete_response"),
    TASK_LIST("task_list"),
    TASK_LIST_RESPONSE("task_list_response"),
    TASK_CANCEL("task_cancel"),
    TASK_COMPLETED("task_completed"),
    TASK_ERROR("task_error"),
    TASK_LOG("task_log"),
    TASK_STATUS_UPDATE("task_status_update"),
    TASK_EXECUTE("task_execute"),
    ERROR("error")
}

// KTCP メッセージの基底インターフェース
interface KtcpMessage {
    val type: KtcpMessageType
    val timestamp: String
}
