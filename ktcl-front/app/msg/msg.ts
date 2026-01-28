import {ClientAuthSuccessMsg} from "./auth";
import {ClientProviderListMsg} from "./provider";

export const ReceiveMsgTypes = {
    auth_success: "auth_success",
    provider_listed: "provider_listed",
} as const
export type ReceiveMsgType = typeof ReceiveMsgTypes[keyof typeof ReceiveMsgTypes]
export type ReceiveMsg = ClientAuthSuccessMsg | ClientProviderListMsg
export const SendMsgTypes = {
    auth_request: "auth_request",
    task_create: "task_create",
    provider_list: "provider_list"
} as const
export type SendMsgType = typeof SendMsgTypes[keyof typeof SendMsgTypes]
