import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerProviderListMsg {
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

export interface ServerProviderCreateMsg {
    type: typeof SendMsgTypes.provider_create
    name: string
    issuer: string
    audience: string
}

export interface ClientProviderCreatedMsg {
    type: typeof ReceiveMsgTypes.provider_created
    providerId: string
}
