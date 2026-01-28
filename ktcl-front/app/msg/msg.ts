import {ClientAuthSuccessMsg} from "./auth";
import {ClientProviderListMsg} from "./provider";
import {ClientQueueCreatedMsg, ClientQueueListedMsg} from "./queue";
import {ClientTaskCreatedMsg, ClientTaskListedMsg, ClientTaskMovedMsg, ClientTaskShowedMsg, ClientTaskUpdatedMsg} from "./task";

export const ReceiveMsgTypes = {
    auth_success: "auth_success",
    provider_listed: "provider_listed",
    queue_created: "queue_created",
    queue_listed: "queue_listed",
    task_showed: "task_showed",
    task_listed: "task_listed",
    task_created: "task_created",
    task_updated: "task_updated",
    task_moved: "task_moved"
} as const
export type ReceiveMsgType = typeof ReceiveMsgTypes[keyof typeof ReceiveMsgTypes]
export type ReceiveMsg = ClientAuthSuccessMsg | ClientProviderListMsg | ClientQueueCreatedMsg |
    ClientQueueListedMsg | ClientTaskShowedMsg | ClientTaskListedMsg | ClientTaskCreatedMsg | ClientTaskUpdatedMsg | ClientTaskMovedMsg

export const SendMsgTypes = {
    auth_request: "auth_request",
    task_create: "task_create",
    provider_list: "provider_list",
    queue_create: "queue_create",
    queue_list: "queue_list",
    task_show: "task_show",
    task_list: "task_list",
    task_update: "task_update",
    task_move: "task_move"
} as const
export type SendMsgType = typeof SendMsgTypes[keyof typeof SendMsgTypes]
