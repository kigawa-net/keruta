import { useCallback, useEffect, useMemo, useState } from "react";
import type { WsState } from "./useWebSocketConnection";
import type { AuthState, KerutaTaskState } from "./ConnectionStateTypes";
import {GlobalState} from "../../app/Global";

/**
 * WebSocket接続状態からKerutaTaskStateを管理するフック
 */
export function useConnectionStateService(globalState: GlobalState): {
  kerutaState: KerutaTaskState;
  setAuthState: (authState: AuthState) => void;
} {
  const [internalAuthState, setInternalAuthState] = useState<AuthState>({ state: "unauthenticated" });

  // WebSocketが再接続された場合、認証状態をリセット
  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => {
    if (globalState.state === "open") {
      setInternalAuthState({ state: "unauthenticated" });
    }
  }, [globalState.state]);

  // wsStateに基づいて認証状態を計算
  // WebSocketがopenでない場合は常にunauthenticated
  const authState: AuthState = useMemo(() => {
    if (globalState.state !== "open") {
      return { state: "unauthenticated" };
    }
    return internalAuthState;
  }, [globalState.state, internalAuthState]);

  // kerutaStateを計算
  const kerutaState: KerutaTaskState = useMemo(() => {
    if (globalState.state === "open") {
      return { state: "connected", auth: authState };
    }
    if (globalState.state === "closed") {
      return { state: "disconnected" };
    }
    return { state: "unloaded" };
  }, [globalState.state, authState]);

  const setAuthState = useCallback((newAuthState: AuthState) => {
    setInternalAuthState(newAuthState);
  }, []);

  return { kerutaState, setAuthState };
}
