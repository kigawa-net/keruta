import { Task, TaskStatus, TaskFilterOptions, TaskSortField, SortOrder } from "./TaskTypes";

/** 有効なタスク状態遷移 */
const validTransitions: Record<TaskStatus, TaskStatus[]> = {
  pending: ["running", "failed"],
  running: ["completed", "failed"],
  completed: [],
  failed: ["pending"],
};

/** 状態遷移が有効かどうかを検証 */
export function isValidTransition(current: TaskStatus, next: TaskStatus): boolean {
  return validTransitions[current].includes(next);
}

/** タスクが終了状態かどうかを判定 */
export function isTerminalStatus(status: TaskStatus): boolean {
  return status === "completed";
}

/** タスクが再試行可能かどうかを判定 */
export function canRetry(status: TaskStatus): boolean {
  return status === "failed";
}

/** タスクのフィルタリング */
export function filterTasks(tasks: Task[], options: TaskFilterOptions): Task[] {
  let filtered = tasks;
  if (options.status) {
    filtered = filtered.filter((task) => task.status === options.status);
  }
  if (options.searchQuery) {
    const query = options.searchQuery.toLowerCase();
    filtered = filtered.filter(
      (task) =>
        task.title.toLowerCase().includes(query) ||
        task.description.toLowerCase().includes(query)
    );
  }
  return filtered;
}

/** タスクのソート */
export function sortTasks(tasks: Task[], field: TaskSortField, order: SortOrder = "asc"): Task[] {
  return [...tasks].sort((a, b) => {
    let comparison = 0;
    switch (field) {
      case "id": comparison = a.id - b.id; break;
      case "title": comparison = a.title.localeCompare(b.title); break;
      case "status": comparison = a.status.localeCompare(b.status); break;
    }
    return order === "asc" ? comparison : -comparison;
  });
}
