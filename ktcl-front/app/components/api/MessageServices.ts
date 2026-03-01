import {WebSocketService} from "./WebSocketService";
import {
    ServerTaskCreateMsg,
    ServerTaskListMsg,
    ServerTaskMoveMsg,
    ServerTaskShowMsg,
    ServerTaskUpdateMsg,
} from "../msg/task";

/** タスク関連のメッセージ送信サービス */
export class TaskMessageService {
    constructor(private wsService: WebSocketService) {
    }

    createTask(queueId: number, title: string, description: string): void {
        const msg: ServerTaskCreateMsg = {type: "task_create", queueId, title, description};
        this.wsService.send(msg);
    }

    listTasks(queueId: number): void {
        const msg: ServerTaskListMsg = {type: "task_list", queueId};
        this.wsService.send(msg);
    }

    showTask(): void {
        const msg: ServerTaskShowMsg = {type: "task_show"};
        this.wsService.send(msg);
    }

    updateTask(taskId: number, status: string): void {
        const msg: ServerTaskUpdateMsg = {type: "task_update", taskId, status};
        this.wsService.send(msg);
    }

    moveTask(taskId: number, targetQueueId: number): void {
        const msg: ServerTaskMoveMsg = {type: "task_move", taskId, targetQueueId};
        this.wsService.send(msg);
    }
}
