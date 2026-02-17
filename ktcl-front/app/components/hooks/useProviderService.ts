import { useAppState } from "./useAppState";
import type { ProviderService } from "../../services";

/**
 * ProviderService を取得するフック
 */
export function useProviderService(): ProviderService {
  return useAppState().providerService;
}
