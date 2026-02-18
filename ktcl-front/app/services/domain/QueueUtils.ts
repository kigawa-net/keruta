import { Queue, QueueFilterOptions, QueueSortField } from "./QueueTypes";
import { SortOrder } from "./TaskTypes";

/** キュー名の最大長 */
export const MAX_QUEUE_NAME_LENGTH = 100;

/** キュー名のバリデーション */
export function validateQueueName(name: string): { valid: boolean; error?: string } {
  const trimmed = name.trim();
  if (trimmed.length === 0) return { valid: false, error: "Queue name is required" };
  if (trimmed.length > MAX_QUEUE_NAME_LENGTH) {
    return { valid: false, error: `Queue name must be at most ${MAX_QUEUE_NAME_LENGTH} characters` };
  }
  if (!/^[a-zA-Z0-9_-]+$/.test(trimmed)) {
    return { valid: false, error: "Queue name can only contain letters, numbers, hyphens, and underscores" };
  }
  return { valid: true };
}

/** キューのフィルタリング */
export function filterQueues(queues: Queue[], options: QueueFilterOptions): Queue[] {
  if (!options.searchQuery) return queues;
  const query = options.searchQuery.toLowerCase();
  return queues.filter((queue) => queue.name.toLowerCase().includes(query));
}

/** キューのソート */
export function sortQueues(queues: Queue[], field: QueueSortField, order: SortOrder = "asc"): Queue[] {
  return [...queues].sort((a, b) => {
    const comparison = field === "id" ? a.id - b.id : a.name.localeCompare(b.name);
    return order === "asc" ? comparison : -comparison;
  });
}
