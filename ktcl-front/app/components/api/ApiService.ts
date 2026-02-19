import { ApiServiceConfig, RequestOptions, ApiResponse } from "./ApiTypes";

/** HTTP通信を抽象化するAPIサービス */
export class ApiService {
  private config: ApiServiceConfig;

  constructor(config: ApiServiceConfig) {
    this.config = config;
  }

  async request<T>(endpoint: string, options: RequestOptions = {}): Promise<ApiResponse<T>> {
    const { method = "GET", headers = {}, body, signal } = options;

    try {
      const authToken = await this.config.getAuthToken?.();
      const requestHeaders: Record<string, string> = {
        "Content-Type": "application/json",
        ...this.config.defaultHeaders,
        ...headers,
      };
      if (authToken) requestHeaders["Authorization"] = `Bearer ${authToken}`;

      const response = await fetch(`${this.config.baseUrl || ""}${endpoint}`, {
        method,
        headers: requestHeaders,
        body: body ? JSON.stringify(body) : undefined,
        signal,
      });

      if (response.status === 401) {
        this.config.onUnauthorized?.();
        return { success: false, error: { message: "Unauthorized", code: "UNAUTHORIZED", status: 401 } };
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        return { success: false, error: { message: errorData.message || response.statusText, status: response.status } };
      }

      const data = await response.json();
      return { success: true, data };
    } catch (error) {
      return { success: false, error: { message: error instanceof Error ? error.message : "Unknown error" } };
    }
  }

  get<T>(endpoint: string, signal?: AbortSignal): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: "GET", signal });
  }

  post<T>(endpoint: string, body: unknown, signal?: AbortSignal): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: "POST", body, signal });
  }

  put<T>(endpoint: string, body: unknown, signal?: AbortSignal): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: "PUT", body, signal });
  }

  delete<T>(endpoint: string, signal?: AbortSignal): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: "DELETE", signal });
  }

  patch<T>(endpoint: string, body: unknown, signal?: AbortSignal): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: "PATCH", body, signal });
  }
}
