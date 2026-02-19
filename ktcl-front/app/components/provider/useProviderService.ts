import { useAppState } from "../app/useAppState";
import type { ProviderService } from "../domain";

/**
 * ProviderService を取得するフック
 */
export function useProviderService(): ProviderService {
  return useAppState().providerService;
}
