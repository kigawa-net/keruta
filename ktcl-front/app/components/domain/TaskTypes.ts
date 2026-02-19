/** タスクの状態 */
export type TaskStatus = "pending" | "running" | "completed" | "failed";

/** タスクエンティティ */
export interface Task {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
}

/** タスク作成の入力データ */
export interface CreateTaskInput {
  queueId: number;
  title: string;
  description: string;
}

/** タスク更新の入力データ */
export interface UpdateTaskInput {
  taskId: number;
  status: TaskStatus;
}

/** タスク移動の入力データ */
export interface MoveTaskInput {
  taskId: number;
  targetQueueId: number;
}

/** タスク一覧の状態 */
export interface TaskListState {
  tasks: Task[];
  isLoading: boolean;
  error: string | null;
}

/** タスクの詳細状態 */
export interface TaskDetailState {
  task: Task | null;
  isLoading: boolean;
  error: string | null;
}

/** タスクフィルタリングのオプション */
export interface TaskFilterOptions {
  status?: TaskStatus;
  searchQuery?: string;
}

/** タスクのソートフィールド */
export type TaskSortField = "id" | "title" | "status";

/** ソート順 */
export type SortOrder = "asc" | "desc";
