import {AuthSuccessMsg} from "./auth";

export const ReceiveMsgTypes = {
    auth_success: "auth_success",
} as const
export type ReceiveMsgType = typeof ReceiveMsgTypes[keyof typeof ReceiveMsgTypes]
export const SendMsgTypes = {
    auth_request: "auth_request",
} as const
export type SendMsgType = typeof SendMsgTypes[keyof typeof SendMsgTypes]
export type ReceiveMsg = AuthSuccessMsg
