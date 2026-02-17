import { useContext } from "react";
import { ServiceContext } from "./ServiceContext";
import type { WsState } from "./useWebSocketConnection";
import type {
  ApiService,
  AuthMessageService,
  ProviderMessageService,
  QueueMessageService,
  TaskMessageService,
  TokenApiService,
  WebSocketService,
} from "../services";

export function useWsState(): WsState {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useWsState must be used within ServiceProvider");
  return context.wsState;
}

export function useWsService(): WebSocketService {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useWsService must be used within ServiceProvider");
  return context.wsService;
}

export function useApiService(): ApiService {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useApiService must be used within ServiceProvider");
  return context.apiService;
}

export function useAuthMessageService(): AuthMessageService {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useAuthMessageService must be used within ServiceProvider");
  return context.authMessageService;
}

export function useTaskMessageService(): TaskMessageService {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useTaskMessageService must be used within ServiceProvider");
  return context.taskMessageService;
}

export function useQueueMessageService(): QueueMessageService {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useQueueMessageService must be used within ServiceProvider");
  return context.queueMessageService;
}

export function useProviderMessageService(): ProviderMessageService {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useProviderMessageService must be used within ServiceProvider");
  return context.providerMessageService;
}

export function useTokenApiService(): TokenApiService {
  const context = useContext(ServiceContext);
  if (!context) throw new Error("useTokenApiService must be used within ServiceProvider");
  return context.tokenApiService;
}
