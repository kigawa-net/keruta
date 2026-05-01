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
        description: string,
        status: string,
        log?: string,
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
    status: string
    log?: string
    type: typeof ReceiveMsgTypes.task_showed
}

export interface ServerTaskUpdateMsg {
    type: typeof SendMsgTypes.task_update
    taskId: number
    status: string
}

export interface ClientTaskUpdatedMsg {
    id: number
    status: string
    log?: string
    type: typeof ReceiveMsgTypes.task_updated
}

export interface ServerTaskMoveMsg {
    type: typeof SendMsgTypes.task_move
    taskId: number
    targetQueueId: number
}

export interface ClientTaskMovedMsg {
    taskId: number
    queueId: number
    type: typeof ReceiveMsgTypes.task_moved
}
