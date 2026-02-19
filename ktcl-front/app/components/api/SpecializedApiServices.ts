import { ApiService } from "./ApiService";
import { ApiResponse } from "./ApiTypes";

/** トークン取得APIサービス */
export class TokenApiService {
  constructor(private apiService: ApiService) {}

  /** サーバートークンの取得 */
  async getServerToken(userToken: string): Promise<ApiResponse<{ token: string }>> {
    return this.apiService.post<{ token: string }>("/api/token", { token: userToken });
  }
}

/** ヘルスチェックAPIサービス */
export class HealthApiService {
  constructor(private apiService: ApiService) {}

  /** サーバーのヘルスチェック */
  async checkHealth(): Promise<ApiResponse<{ status: string }>> {
    return this.apiService.get<{ status: string }>("/health");
  }
}
