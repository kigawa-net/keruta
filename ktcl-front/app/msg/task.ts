import {ReceiveMsgTypes, SendMsgTypes} from "./msg";

export interface ServerTaskCreateMsg {
    type: typeof SendMsgTypes.task_create
    queueId: number
    title: string,
    description: string
}

export interface ServerTaskListMsg {
    type: typeof SendMsgTypes.task_list
    queueId: number
}


export interface ClientTaskCreatedMsg {
    id: number;
    type: typeof ReceiveMsgTypes.task_created
}

export interface ClientTaskListedMsg {
    tasks: {
        id: number,
        title: string,
        description: string
    }[];
    type: typeof ReceiveMsgTypes.task_listed
}

export interface ServerTaskShowMsg {
    type: typeof SendMsgTypes.task_show
}

export interface ClientTaskShowedMsg {
    id: number,
    title: string
    description: string
    type: typeof ReceiveMsgTypes.task_showed
}
