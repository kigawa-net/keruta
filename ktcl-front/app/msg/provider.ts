import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerProvidersRequestMsg {
    type: typeof SendMsgTypes.provider_list
}

export interface ClientProviderListMsg {
    providers: {
        name: string
        id: string
        issuer: string
        audience: string
    }[]
    type: typeof ReceiveMsgTypes.provider_listed
}
