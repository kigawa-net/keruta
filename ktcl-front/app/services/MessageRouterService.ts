import { useEffect } from "react";
import { TaskService, QueueService, ProviderService } from "./domain";
import { KerutaTaskState } from "./ConnectionStateService";
import type { WsState } from "../components/useWebSocketConnection";

export interface MessageRouterDependencies {
  wsState: WsState;
  kerutaState: KerutaTaskState;
  taskService: TaskService;
  queueService: QueueService;
  providerService: ProviderService;
}

/**
 * WebSocketメッセージを各ドメインサービスにルーティングする
 * WebSocketがopenかつconnected状態の時のみメッセージを処理する
 */
export function useMessageRouterService(deps: MessageRouterDependencies): void {
  const { wsState, kerutaState, taskService, queueService, providerService } = deps;

  useEffect(() => {
    if (wsState.state !== "open" || kerutaState.state !== "connected") return;

    const ws = wsState.websocket;

    const handler = (event: MessageEvent) => {
      const msg = JSON.parse(event.data);
      taskService.handleMessage(msg);
      queueService.handleMessage(msg);
      providerService.handleMessage(msg);
    };

    ws.addEventListener("message", handler);
    return () => ws.removeEventListener("message", handler);
  }, [wsState.state, wsState, kerutaState.state, taskService, queueService, providerService]);
}
