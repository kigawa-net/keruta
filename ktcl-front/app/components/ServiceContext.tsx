import { createContext, ReactNode, useMemo } from "react";
import {
  ApiService,
  AuthMessageService,
  ProviderMessageService,
  QueueMessageService,
  TaskMessageService,
  TokenApiService,
  WebSocketService,
} from "../services";
import { useWebSocketConnection, WsState } from "./useWebSocketConnection";

interface Services {
  wsService: WebSocketService;
  apiService: ApiService;
  authMessageService: AuthMessageService;
  taskMessageService: TaskMessageService;
  queueMessageService: QueueMessageService;
  providerMessageService: ProviderMessageService;
  tokenApiService: TokenApiService;
}

export interface ServiceContextValue extends Services {
  wsState: WsState;
}

export const ServiceContext = createContext<ServiceContextValue | null>(null);

export interface ServiceProviderProps {
  wsUrl: URL;
  apiBaseUrl?: string;
  children: ReactNode;
  getAuthToken?: () => Promise<string | null>;
  onUnauthorized?: () => void;
}

export function ServiceProvider({
  wsUrl,
  apiBaseUrl,
  children,
  getAuthToken,
  onUnauthorized,
}: ServiceProviderProps) {
  const wsState = useWebSocketConnection(wsUrl);

  const services = useMemo(() => {
    const wsService = new WebSocketService({ url: wsUrl });
    const apiService = new ApiService({
      baseUrl: apiBaseUrl,
      getAuthToken,
      onUnauthorized,
    });
    return {
      wsService,
      apiService,
      authMessageService: new AuthMessageService(wsService),
      taskMessageService: new TaskMessageService(wsService),
      queueMessageService: new QueueMessageService(wsService),
      providerMessageService: new ProviderMessageService(wsService),
      tokenApiService: new TokenApiService(apiService),
    };
  }, [wsUrl, apiBaseUrl, getAuthToken, onUnauthorized]);

  const contextValue = useMemo<ServiceContextValue>(
    () => ({ ...services, wsState }),
    [services, wsState]
  );

  return (
    <ServiceContext.Provider value={contextValue}>{children}</ServiceContext.Provider>
  );
}

export type { WsState, WebsocketOpenState } from "./useWebSocketConnection";

