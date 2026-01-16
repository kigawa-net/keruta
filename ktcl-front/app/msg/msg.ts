import {ClientAuthSuccessMsg} from "./auth";

export const ReceiveMsgTypes = {
    auth_success: "auth_success",
    provider_list: "provider_list",
} as const
export type ReceiveMsgType = typeof ReceiveMsgTypes[keyof typeof ReceiveMsgTypes]
export const SendMsgTypes = {
    auth_request: "auth_request",
    task_create: "task_create",
    providers_request: "providers_request"
} as const
export type SendMsgType = typeof SendMsgTypes[keyof typeof SendMsgTypes]
export type ReceiveMsg = ClientAuthSuccessMsg
