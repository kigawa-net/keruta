import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerTaskCreateMsg {
    type: typeof SendMsgTypes.task_create
    queueId: number
    name: string
}

export interface ServerTaskListMsg {
    type: typeof SendMsgTypes.task_list
}


export interface ClientTaskCreatedMsg {
    id: number;
    type: typeof ReceiveMsgTypes.task_created
}

export interface ClientTaskListedMsg {
    queues: {
        id: number,
        name: string
    }[];
    type: typeof ReceiveMsgTypes.task_listed
}
export interface ServerTaskShowMsg {
    type: typeof SendMsgTypes.task_show
}
export interface ClientTaskShowedMsg {
        id: number,
        name: string
    type: typeof ReceiveMsgTypes.task_showed
}
