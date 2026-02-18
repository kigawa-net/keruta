import { Provider, ProviderFilterOptions, ProviderSortField, CreateProviderInput } from "./ProviderTypes";
import { SortOrder } from "./TaskTypes";

/** プロバイダー名の最大長 */
export const MAX_PROVIDER_NAME_LENGTH = 50;

/** プロバイダー名のバリデーション */
export function validateProviderName(name: string): { valid: boolean; error?: string } {
  const trimmed = name.trim();
  if (trimmed.length === 0) return { valid: false, error: "Provider name is required" };
  if (trimmed.length > MAX_PROVIDER_NAME_LENGTH) {
    return { valid: false, error: `Provider name must be at most ${MAX_PROVIDER_NAME_LENGTH} characters` };
  }
  return { valid: true };
}

/** Issuer URLのバリデーション */
export function validateIssuer(issuer: string): { valid: boolean; error?: string } {
  const trimmed = issuer.trim();
  if (trimmed.length === 0) return { valid: false, error: "Issuer URL is required" };
  try { new URL(trimmed); } catch { return { valid: false, error: "Invalid issuer URL format" }; }
  return { valid: true };
}

/** Audienceのバリデーション */
export function validateAudience(audience: string): { valid: boolean; error?: string } {
  const trimmed = audience.trim();
  if (trimmed.length === 0) return { valid: false, error: "Audience is required" };
  return { valid: true };
}

/** プロバイダー作成の全バリデーション */
export function validateCreateInput(input: CreateProviderInput): { valid: boolean; errors: string[] } {
  const errors: string[] = [];
  const nameResult = validateProviderName(input.name);
  if (!nameResult.valid && nameResult.error) errors.push(nameResult.error);
  const issuerResult = validateIssuer(input.issuer);
  if (!issuerResult.valid && issuerResult.error) errors.push(issuerResult.error);
  const audienceResult = validateAudience(input.audience);
  if (!audienceResult.valid && audienceResult.error) errors.push(audienceResult.error);
  return { valid: errors.length === 0, errors };
}

/** プロバイダーのフィルタリング */
export function filterProviders(providers: Provider[], options: ProviderFilterOptions): Provider[] {
  if (!options.searchQuery) return providers;
  const query = options.searchQuery.toLowerCase();
  return providers.filter(
    (p) => p.name.toLowerCase().includes(query) || p.issuer.toLowerCase().includes(query)
  );
}

/** プロバイダーのソート */
export function sortProviders(providers: Provider[], field: ProviderSortField, order: SortOrder = "asc"): Provider[] {
  return [...providers].sort((a, b) => {
    const comparison = field === "name" ? a.name.localeCompare(b.name) : a.issuer.localeCompare(b.issuer);
    return order === "asc" ? comparison : -comparison;
  });
}
