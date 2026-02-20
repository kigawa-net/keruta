import { useContext } from "react";
import { ServiceContext } from "./ServiceContext";
import type { WsState } from "../net/websocket/useWebSocketConnection";
import type {
  ApiService,
  AuthMessageService,
  ProviderMessageService,
  QueueMessageService,
  TaskMessageService,
  TokenApiService,
  WebSocketService,
} from "../api";

const defaultWsState: WsState = { state: "unloaded" }

export function useWsState(): WsState {
  const context = useContext(ServiceContext);
  if (!context) return defaultWsState;
  return context.wsState;
}

export function useWsService(): WebSocketService | null {
  const context = useContext(ServiceContext);
  if (!context) return null;
  return context.wsService;
}

export function useApiService(): ApiService | null {
  const context = useContext(ServiceContext);
  if (!context) return null;
  return context.apiService;
}

export function useAuthMessageService(): AuthMessageService | null {
  const context = useContext(ServiceContext);
  if (!context) return null;
  return context.authMessageService;
}

export function useTaskMessageService(): TaskMessageService | null {
  const context = useContext(ServiceContext);
  if (!context) return null;
  return context.taskMessageService;
}

export function useQueueMessageService(): QueueMessageService | null {
  const context = useContext(ServiceContext);
  if (!context) return null;
  return context.queueMessageService;
}

export function useProviderMessageService(): ProviderMessageService | null {
  const context = useContext(ServiceContext);
  if (!context) return null;
  return context.providerMessageService;
}

export function useTokenApiService(): TokenApiService | null {
  const context = useContext(ServiceContext);
  if (!context) return null;
  return context.tokenApiService;
}
