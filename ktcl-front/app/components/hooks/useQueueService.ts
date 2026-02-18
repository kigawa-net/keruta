import { useAppState } from "./useAppState";
import type { QueueService } from "../../services";

/**
 * QueueService を取得するフック
 */
export function useQueueService(): QueueService {
  return useAppState().queueService;
}
