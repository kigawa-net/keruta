import { useAppState } from "./useAppState";
import type { TaskService } from "../../services";

/**
 * TaskService を取得するフック
 */
export function useTaskService(): TaskService {
  return useAppState().taskService;
}
