import { TaskMessageService } from "../api";
import { ReceiveMsg } from "../msg/msg";
import {
  ClientTaskCreatedMsg,
  ClientTaskListedMsg,
  ClientTaskUpdatedMsg,
  ClientTaskMovedMsg,
} from "../msg/task";
import { Task, CreateTaskInput, UpdateTaskInput, MoveTaskInput } from "./TaskTypes";

/** タスク関連のビジネスロジックを管理するドメインサービス */
export class TaskService {
  private taskListCallbacks: Set<(tasks: Task[]) => void> = new Set();
  private taskCreatedCallbacks: Set<(taskId: number) => void> = new Set();
  private taskUpdatedCallbacks: Set<(taskId: number, status: string) => void> = new Set();
  private taskMovedCallbacks: Set<(taskId: number, queueId: number) => void> = new Set();

  constructor(private taskMessageService: TaskMessageService) {}

  createTask(input: CreateTaskInput): void {
    if (!input.title.trim()) throw new Error("Task title is required");
    if (!input.description.trim()) throw new Error("Task description is required");
    if (input.queueId <= 0) throw new Error("Invalid queue ID");
    this.taskMessageService.createTask(input.queueId, input.title.trim(), input.description.trim());
  }

  listTasks(queueId: number): void {
    if (queueId <= 0) throw new Error("Invalid queue ID");
    this.taskMessageService.listTasks(queueId);
  }

  showTask(taskId: number): void {
    if (taskId <= 0) throw new Error("Invalid task ID");
    this.taskMessageService.showTask();
  }

  updateTask(input: UpdateTaskInput): void {
    if (input.taskId <= 0) throw new Error("Invalid task ID");
    this.taskMessageService.updateTask(input.taskId, input.status);
  }

  moveTask(input: MoveTaskInput): void {
    if (input.taskId <= 0) throw new Error("Invalid task ID");
    if (input.targetQueueId <= 0) throw new Error("Invalid target queue ID");
    this.taskMessageService.moveTask(input.taskId, input.targetQueueId);
  }

  handleMessage(message: ReceiveMsg): void {
    switch (message.type) {
      case "task_listed": this.handleTaskListed(message as ClientTaskListedMsg); break;
      case "task_created": this.handleTaskCreated(message as ClientTaskCreatedMsg); break;
      case "task_updated": this.handleTaskUpdated(message as ClientTaskUpdatedMsg); break;
      case "task_moved": this.handleTaskMoved(message as ClientTaskMovedMsg); break;
    }
  }

  onTaskList(callback: (tasks: Task[]) => void): () => void {
    this.taskListCallbacks.add(callback);
    return () => this.taskListCallbacks.delete(callback);
  }

  onTaskCreated(callback: (taskId: number) => void): () => void {
    this.taskCreatedCallbacks.add(callback);
    return () => this.taskCreatedCallbacks.delete(callback);
  }

  onTaskUpdated(callback: (taskId: number, status: string) => void): () => void {
    this.taskUpdatedCallbacks.add(callback);
    return () => this.taskUpdatedCallbacks.delete(callback);
  }

  onTaskMoved(callback: (taskId: number, queueId: number) => void): () => void {
    this.taskMovedCallbacks.add(callback);
    return () => this.taskMovedCallbacks.delete(callback);
  }

  private handleTaskListed(message: ClientTaskListedMsg): void {
    const tasks: Task[] = message.tasks.map((t) => ({
      id: t.id, title: t.title, description: t.description, status: t.status as Task["status"],
    }));
    this.taskListCallbacks.forEach((cb) => cb(tasks));
  }

  private handleTaskCreated(message: ClientTaskCreatedMsg): void {
    this.taskCreatedCallbacks.forEach((cb) => cb(message.id));
  }

  private handleTaskUpdated(message: ClientTaskUpdatedMsg): void {
    this.taskUpdatedCallbacks.forEach((cb) => cb(message.id, message.status));
  }

  private handleTaskMoved(message: ClientTaskMovedMsg): void {
    this.taskMovedCallbacks.forEach((cb) => cb(message.taskId, message.queueId));
  }
}
