import { WebSocketService } from "./WebSocketService";
import { ServerAuthRequestMsg } from "../../msg/auth";
import {
  ServerTaskCreateMsg,
  ServerTaskListMsg,
  ServerTaskShowMsg,
  ServerTaskUpdateMsg,
  ServerTaskMoveMsg,
} from "../../msg/task";
import { ServerQueueCreateMsg, ServerQueueListMsg } from "../../msg/queue";
import {
  ServerProviderListMsg,
  ServerProviderAddMsg,
  ServerProviderCompleteMsg,
  ServerProviderDeleteMsg,
} from "../../msg/provider";

/** 認証メッセージ送信サービス */
export class AuthMessageService {
  constructor(private wsService: WebSocketService) {}

  sendAuthRequest(userToken: string, serverToken: string): void {
    if (!this.wsService.isConnected()) return;
    const msg: ServerAuthRequestMsg = {
      type: "auth_request",
      userToken,
      serverToken,
    };
    this.wsService.send(msg);
  }
}

/** タスク関連のメッセージ送信サービス */
export class TaskMessageService {
  constructor(private wsService: WebSocketService) {}

  createTask(queueId: number, title: string, description: string): void {
    const msg: ServerTaskCreateMsg = { type: "task_create", queueId, title, description };
    this.wsService.send(msg);
  }

  listTasks(queueId: number): void {
    const msg: ServerTaskListMsg = { type: "task_list", queueId };
    this.wsService.send(msg);
  }

  showTask(): void {
    const msg: ServerTaskShowMsg = { type: "task_show" };
    this.wsService.send(msg);
  }

  updateTask(taskId: number, status: string): void {
    const msg: ServerTaskUpdateMsg = { type: "task_update", taskId, status };
    this.wsService.send(msg);
  }

  moveTask(taskId: number, targetQueueId: number): void {
    const msg: ServerTaskMoveMsg = { type: "task_move", taskId, targetQueueId };
    this.wsService.send(msg);
  }
}

/** キュー関連のメッセージ送信サービス */
export class QueueMessageService {
  constructor(private wsService: WebSocketService) {}

  createQueue(providerId: number, name: string): void {
    const msg: ServerQueueCreateMsg = { type: "queue_create", providerId, name };
    this.wsService.send(msg);
  }

  listQueues(): void {
    const msg: ServerQueueListMsg = { type: "queue_list" };
    this.wsService.send(msg);
  }
}

/** プロバイダー関連のメッセージ送信サービス */
export class ProviderMessageService {
  constructor(private wsService: WebSocketService) {}

  listProviders(): void {
    const msg: ServerProviderListMsg = { type: "provider_list" };
    this.wsService.send(msg);
  }

  addProvider(name: string, issuer: string, audience: string): void {
    const msg: ServerProviderAddMsg = { type: "provider_add", name, issuer, audience };
    this.wsService.send(msg);
  }

  completeProviderAdd(token: string, code: string, redirectUri: string): void {
    const msg: ServerProviderCompleteMsg = { type: "provider_complete", token, code, redirectUri };
    this.wsService.send(msg);
  }

  deleteProvider(id: string): void {
    const msg: ServerProviderDeleteMsg = { type: "provider_delete", id };
    this.wsService.send(msg);
  }
}
