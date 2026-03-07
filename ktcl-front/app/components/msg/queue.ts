import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerQueueCreateMsg {
    type: typeof SendMsgTypes.queue_create
    providerId: number
    name: string
}

export interface ServerQueueListMsg {
    type: typeof SendMsgTypes.queue_list
}

export interface ServerQueueShowMsg {
    type: typeof SendMsgTypes.queue_show
    id: number
}

export interface ServerQueueUpdateMsg {
    type: typeof SendMsgTypes.queue_update
    queueId: number
    name: string
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

export interface ClientQueueShowedMsg {
    id: number;
    name: string;
    type: typeof ReceiveMsgTypes.queue_showed
}

export interface ClientQueueUpdatedMsg {
    id: number;
    name: string;
    type: typeof ReceiveMsgTypes.queue_updated
}
