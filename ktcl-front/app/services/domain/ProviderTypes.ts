/** IDP（Identity Provider）エンティティ */
export interface Idp {
  issuer: string;
  subject: string;
  audience: string;
}

/** プロバイダーエンティティ */
export interface Provider {
  id: string;
  name: string;
  issuer: string;
  audience: string;
  idps: Idp[];
}

/** プロバイダー作成の入力データ */
export interface CreateProviderInput {
  name: string;
  issuer: string;
  audience: string;
}

/** プロバイダー完了の入力データ */
export interface CompleteProviderInput {
  token: string;
  code: string;
  redirectUri: string;
}

/** プロバイダー一覧の状態 */
export interface ProviderListState {
  providers: Provider[];
  isLoading: boolean;
  error: string | null;
}

/** プロバイダー追加の状態 */
export interface ProviderAddState {
  token: string | null;
  isProcessing: boolean;
  error: string | null;
}

/** プロバイダーフィルタリングのオプション */
export interface ProviderFilterOptions {
  searchQuery?: string;
}

/** プロバイダーのソートフィールド */
export type ProviderSortField = "name" | "issuer";
