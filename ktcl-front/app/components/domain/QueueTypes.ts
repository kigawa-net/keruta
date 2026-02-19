/** キューエンティティ */
export interface Queue {
  id: number;
  name: string;
}

/** キュー作成の入力データ */
export interface CreateQueueInput {
  providerId: number;
  name: string;
}

/** キュー一覧の状態 */
export interface QueueListState {
  queues: Queue[];
  isLoading: boolean;
  error: string | null;
}

/** キューフィルタリングのオプション */
export interface QueueFilterOptions {
  searchQuery?: string;
}

/** キューのソートフィールド */
export type QueueSortField = "id" | "name";
