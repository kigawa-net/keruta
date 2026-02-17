import { useEffect, useState } from "react";
import type { WsState } from "../components/useWebSocketConnection";
import type { KerutaTaskState } from "./ConnectionStateTypes";

/**
 * WebSocket接続状態からKerutaTaskStateを管理するフック
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
