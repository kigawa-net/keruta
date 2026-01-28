import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerQueueCreateMsg {
    type: typeof SendMsgTypes.queue_create
    providerId: number
    name: string
}

export interface ClientQueueCreatedMsg {
    queueId: number;
    type: typeof ReceiveMsgTypes.queue_created
}
