import {ReceiveMsgTypes} from "./msg";

export interface AuthRequestMsg {
    token: string,
    type: typeof ReceiveMsgTypes.auth_request
}

export interface AuthSuccessMsg {
    type: typeof ReceiveMsgTypes.auth_success
}
