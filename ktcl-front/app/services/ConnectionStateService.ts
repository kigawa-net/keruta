import { useEffect, useState } from "react";
import type { WsState } from "../components/useWebSocketConnection";

export type KerutaTaskState =
  | { state: "unloaded" }
  | ConnectedKerutaTaskState
  | { state: "disconnected" };

export interface ConnectedKerutaTaskState {
  state: "connected";
  auth: AuthState;
}

export type AuthState = { state: "unauthenticated" } | { state: "authenticated" };

/**
 * WebSocket接続状態からKerutaTaskStateを管理する
 */
export function useConnectionStateService(wsState: WsState): KerutaTaskState {
  const [kerutaState, setKerutaState] = useState<KerutaTaskState>({ state: "unloaded" });

  useEffect(() => {
    const wsStateValue = wsState.state;
    if (wsStateValue === "open" && kerutaState.state !== "connected") {
      setKerutaState({ state: "connected", auth: { state: "unauthenticated" } });
    } else if (wsStateValue === "closed") {
      setKerutaState({ state: "disconnected" });
    } else if (wsStateValue !== "open" && kerutaState.state === "connected") {
      setKerutaState({ state: "unloaded" });
    }
  }, [wsState.state, kerutaState.state]);

  return kerutaState;
}
