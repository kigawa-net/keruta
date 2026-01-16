import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerProvidersRequestMsg {
    type: typeof SendMsgTypes.providers_request
}

export interface ClientProviderListMsg {
    providers: {
        name: string
        id: string
        issuer: string
        audience: string
    }[]
    type: typeof ReceiveMsgTypes.auth_success
}
