import { createContext, ReactNode, useContext, useMemo } from "react";
import {
  TaskService,
  QueueService,
  ProviderService,
} from "../services";
import { useConnectionStateService } from "../services";
import { useMessageRouterService } from "../services";
import {
  useWsState,
  useTaskMessageService,
  useQueueMessageService,
  useProviderMessageService,
} from "./useServiceHooks";
import WsSender from "./websocket/WsSender";
import type { KerutaTaskState } from "../services";

interface AppState {
  kerutaState: KerutaTaskState;
  taskService: TaskService;
  queueService: QueueService;
  providerService: ProviderService;
}

const AppContext = createContext<AppState | null>(null);

export function AppProvider({ children }: { children: ReactNode }) {
  const wsState = useWsState();
  const taskMsgService = useTaskMessageService();
  const queueMsgService = useQueueMessageService();
  const providerMsgService = useProviderMessageService();

  // Domain services
  const services = useMemo(
    () => ({
      taskService: new TaskService(taskMsgService),
      queueService: new QueueService(queueMsgService),
      providerService: new ProviderService(providerMsgService),
    }),
    [taskMsgService, queueMsgService, providerMsgService]
  );

  // Connection state management
  const kerutaState = useConnectionStateService(wsState);

  // Message routing
  useMessageRouterService({
    wsState,
    kerutaState,
    taskService: services.taskService,
    queueService: services.queueService,
    providerService: services.providerService,
  });

  const appState: AppState = {
    kerutaState,
    taskService: services.taskService,
    queueService: services.queueService,
    providerService: services.providerService,
  };

  return (
    <AppContext.Provider value={appState}>
      <WsSender />
      {children}
    </AppContext.Provider>
  );
}

export function useAppState(): AppState {
  const context = useContext(AppContext);
  if (!context) throw new Error("useAppState must be used within AppProvider");
  return context;
}

export function useKerutaTaskState(): KerutaTaskState {
  return useAppState().kerutaState;
}

export function useTaskService(): TaskService {
  return useAppState().taskService;
}

export function useQueueService(): QueueService {
  return useAppState().queueService;
}

export function useProviderService(): ProviderService {
  return useAppState().providerService;
}

// Re-export types for convenience
export type { KerutaTaskState, ConnectedKerutaTaskState, AuthState } from "../services/ConnectionStateTypes";
