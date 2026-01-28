import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerQueueCreateMsg {
    type: typeof SendMsgTypes.queue_create
    providerId: number
    name: string
}

export interface ServerQueueListMsg {
    type: typeof SendMsgTypes.queue_list
}

export interface ClientQueueCreatedMsg {
    queueId: number;
    type: typeof ReceiveMsgTypes.queue_created
}

export interface ClientQueueListedMsg {
    queues: {
        id: number,
        name: string
    }[];
    type: typeof ReceiveMsgTypes.queue_listed
}
