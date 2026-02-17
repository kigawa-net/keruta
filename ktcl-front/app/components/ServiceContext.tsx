import { createContext, ReactNode, useContext, useMemo } from "react";
import {
  WebSocketService,
  ApiService,
  AuthMessageService,
  TaskMessageService,
  QueueMessageService,
  ProviderMessageService,
  TokenApiService,
} from "../services";

interface Services {
  wsService: WebSocketService;
  apiService: ApiService;
  authMessageService: AuthMessageService;
  taskMessageService: TaskMessageService;
  queueMessageService: QueueMessageService;
  providerMessageService: ProviderMessageService;
  tokenApiService: TokenApiService;
}

const ServiceContext = createContext<Services | null>(null);

export interface ServiceProviderProps {
  wsUrl: URL;
  apiBaseUrl?: string;
  children: ReactNode;
  getAuthToken?: () => Promise<string | null>;
  onUnauthorized?: () => void;
}

export function ServiceProvider({
  wsUrl,
  apiBaseUrl = "",
  children,
  getAuthToken,
  onUnauthorized,
}: ServiceProviderProps) {
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

  return (
    <ServiceContext.Provider value={services}>{children}</ServiceContext.Provider>
  );
}

export function useServices(): Services {
  const services = useContext(ServiceContext);
  if (!services) throw new Error("useServices must be used within ServiceProvider");
  return services;
}

export function useWsService(): WebSocketService {
  return useServices().wsService;
}

export function useApiService(): ApiService {
  return useServices().apiService;
}

export function useAuthMessageService(): AuthMessageService {
  return useServices().authMessageService;
}

export function useTaskMessageService(): TaskMessageService {
  return useServices().taskMessageService;
}

export function useQueueMessageService(): QueueMessageService {
  return useServices().queueMessageService;
}

export function useProviderMessageService(): ProviderMessageService {
  return useServices().providerMessageService;
}

export function useTokenApiService(): TokenApiService {
  return useServices().tokenApiService;
}
