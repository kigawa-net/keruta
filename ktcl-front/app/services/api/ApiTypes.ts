/** API エラーレスポンス */
export interface ApiError {
  message: string;
  code?: string;
  status?: number;
}

/** API レスポンス */
export type ApiResponse<T> =
  | { success: true; data: T }
  | { success: false; error: ApiError };

/** HTTP リクエストのオプション */
export interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE" | "PATCH";
  headers?: Record<string, string>;
  body?: unknown;
  signal?: AbortSignal;
}

/** APIサービスの設定 */
export interface ApiServiceConfig {
  baseUrl: string;
  defaultHeaders?: Record<string, string>;
  onUnauthorized?: () => void;
  getAuthToken?: () => Promise<string | null>;
}
