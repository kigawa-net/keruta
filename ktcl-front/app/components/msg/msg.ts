import {ClientAuthSuccessMsg} from "./auth";
import {
    ClientProviderAddTokenMsg,
    ClientProviderDeletedMsg,
    ClientProviderIdpAddedMsg,
    ClientProviderListMsg
} from "./provider";
import {ClientQueueCreatedMsg, ClientQueueListedMsg} from "./queue";
import {
    ClientTaskCreatedMsg,
    ClientTaskListedMsg,
    ClientTaskMovedMsg,
    ClientTaskShowedMsg,
    ClientTaskUpdatedMsg
} from "./task";

export const ReceiveMsgTypes = {
    auth_success: "auth_success",
    provider_listed: "provider_listed",
    provider_add_token_issued: "provider_add_token_issued",
    provider_idp_added: "provider_idp_added",
    provider_deleted: "provider_deleted",
    queue_created: "queue_created",
    queue_listed: "queue_listed",
    task_showed: "task_showed",
    task_listed: "task_listed",
    task_created: "task_created",
    task_updated: "task_updated",
    task_moved: "task_moved"
} as const
export type ReceiveMsgType = typeof ReceiveMsgTypes[keyof typeof ReceiveMsgTypes]
export type ReceiveMsg =
    ClientAuthSuccessMsg
    | ClientProviderListMsg
    | ClientProviderAddTokenMsg
    | ClientProviderIdpAddedMsg
    | ClientProviderDeletedMsg
    | ClientQueueCreatedMsg
    | ClientQueueListedMsg
    | ClientTaskShowedMsg
    | ClientTaskListedMsg
    | ClientTaskCreatedMsg
    | ClientTaskUpdatedMsg
    | ClientTaskMovedMsg

export const SendMsgTypes = {
    auth_request: "auth_request",
    task_create: "task_create",
    provider_list: "provider_list",
    provider_issue_token: "provider_issue_token",
    provider_complete: "provider_complete",
    provider_delete: "provider_delete",
    queue_create: "queue_create",
    queue_list: "queue_list",
    task_show: "task_show",
    task_list: "task_list",
    task_update: "task_update",
    task_move: "task_move"
} as const
export type SendMsgType = typeof SendMsgTypes[keyof typeof SendMsgTypes]
