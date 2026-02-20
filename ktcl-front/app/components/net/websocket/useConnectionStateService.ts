import { useCallback, useEffect, useMemo, useState } from "react";
import type { WsState } from "./useWebSocketConnection";
import type { AuthState, KerutaTaskState } from "./ConnectionStateTypes";

/**
 * WebSocket接続状態からKerutaTaskStateを管理するフック
 */
export function useConnectionStateService(wsState: WsState): {
  kerutaState: KerutaTaskState;
  setAuthState: (authState: AuthState) => void;
} {
  const [internalAuthState, setInternalAuthState] = useState<AuthState>({ state: "unauthenticated" });

  // WebSocketが再接続された場合、認証状態をリセット
  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => {
    if (wsState.state === "open") {
      setInternalAuthState({ state: "unauthenticated" });
    }
  }, [wsState.state]);

  // wsStateに基づいて認証状態を計算
  // WebSocketがopenでない場合は常にunauthenticated
  const authState: AuthState = useMemo(() => {
    if (wsState.state !== "open") {
      return { state: "unauthenticated" };
    }
    return internalAuthState;
  }, [wsState.state, internalAuthState]);

  // kerutaStateを計算
  const kerutaState: KerutaTaskState = useMemo(() => {
    if (wsState.state === "open") {
      return { state: "connected", auth: authState };
    }
    if (wsState.state === "closed") {
      return { state: "disconnected" };
    }
    return { state: "unloaded" };
  }, [wsState.state, authState]);

  const setAuthState = useCallback((newAuthState: AuthState) => {
    setInternalAuthState(newAuthState);
  }, []);

  return { kerutaState, setAuthState };
}
