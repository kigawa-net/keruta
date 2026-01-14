import {SendMsgTypes} from "./msg";

export interface TaskCreateMsg {
    name: string,
    queueId: string
    type: typeof SendMsgTypes.task_create
}
