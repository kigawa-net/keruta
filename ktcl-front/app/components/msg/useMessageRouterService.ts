import { useEffect } from "react";
import { TaskService, QueueService, ProviderService } from "../domain";
import { KerutaTaskState } from "../net/websocket/ConnectionStateTypes";
import { createMessageHandler } from "./MessageRouterService";
import type { WsState } from "../net/websocket/useWebSocketConnection";
import {GlobalState} from "../app/Global";

export interface UseMessageRouterServiceParams {
  globalState: GlobalState;
  kerutaState: KerutaTaskState;
  taskService: TaskService;
  queueService: QueueService;
  providerService: ProviderService;
  onAuthSuccess: () => void;
}

/**
 * WebSocketメッセージを各ドメインサービスにルーティングするフック
 * WebSocketがopenかつconnected状態の時のみメッセージを処理する
 */
export function useMessageRouterService(params: UseMessageRouterServiceParams): void {
  const { globalState, kerutaState, taskService, queueService, providerService, onAuthSuccess } = params;

  useEffect(() => {
    if (globalState.state !== "open" || kerutaState.state !== "connected") return;

    const ws = globalState.websocket;
    const handler = createMessageHandler({
      ws,
      taskService,
      queueService,
      providerService,
      onAuthSuccess,
    });

    ws.addEventListener("message", handler);
    return () => ws.removeEventListener("message", handler);
  }, [globalState.state, globalState, kerutaState.state, taskService, queueService, providerService, onAuthSuccess]);
}
