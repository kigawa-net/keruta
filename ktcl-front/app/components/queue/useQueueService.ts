import { useAppState } from "../app/useAppState";
import type { QueueService } from "../domain";

/**
 * QueueService を取得するフック
 */
export function useQueueService(): QueueService {
  return useAppState().queueService;
}
