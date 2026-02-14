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
        idps: {
            issuer: string
            subject: string
            audience: string
        }[]
    }[]
    type: typeof ReceiveMsgTypes.provider_listed
}

export interface ServerProviderAddMsg {
    type: typeof SendMsgTypes.provider_add
    name: string
    issuer: string
    audience: string
}

export interface ClientProviderAddTokenMsg {
    type: typeof ReceiveMsgTypes.provider_add_token_issued
    token: string
}

export interface ServerProviderCompleteMsg {
    type: typeof SendMsgTypes.provider_complete
    token: string
    code: string
    redirectUri: string
}

export interface ClientProviderIdpAddedMsg {
    type: typeof ReceiveMsgTypes.provider_idp_added
}

