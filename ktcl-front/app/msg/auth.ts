import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface AuthRequestMsg {
    token: string,
    type: typeof SendMsgTypes.auth_request
}

export interface AuthSuccessMsg {
    type: typeof ReceiveMsgTypes.auth_success
}
