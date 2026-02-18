import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerAuthRequestMsg {
    userToken: string,
    serverToken: string,
    type: typeof SendMsgTypes.auth_request
}

export interface ClientAuthSuccessMsg {
    type: typeof ReceiveMsgTypes.auth_success
}
