import { useAppState } from "../app/useAppState";
import type { TaskService } from "../domain";

/**
 * TaskService を取得するフック
 */
export function useTaskService(): TaskService {
  return useAppState().taskService;
}
