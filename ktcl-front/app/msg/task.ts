import {SendMsgTypes} from "./msg";

export interface TaskCreateMsg {
    name: string,
    type: typeof SendMsgTypes.task_create
}
