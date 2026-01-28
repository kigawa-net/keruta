import {ClientAuthSuccessMsg} from "./auth";
import {ClientProviderListMsg} from "./provider";
import {ClientQueueCreatedMsg} from "./queue";

export const ReceiveMsgTypes = {
    auth_success: "auth_success",
    provider_listed: "provider_listed",
    queue_created: "queue_created"
} as const
export type ReceiveMsgType = typeof ReceiveMsgTypes[keyof typeof ReceiveMsgTypes]
export type ReceiveMsg = ClientAuthSuccessMsg | ClientProviderListMsg | ClientQueueCreatedMsg
export const SendMsgTypes = {
    auth_request: "auth_request",
    task_create: "task_create",
    provider_list: "provider_list",
    queue_create: "queue_create"
} as const
export type SendMsgType = typeof SendMsgTypes[keyof typeof SendMsgTypes]
