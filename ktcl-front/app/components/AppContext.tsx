import { createContext, ReactNode, useContext, useEffect, useMemo, useState } from "react";
import { TaskService, QueueService, ProviderService } from "../services";
import {
  useWsState,
  useTaskMessageService,
  useQueueMessageService,
  useProviderMessageService,
} from "./useServiceHooks";
import WsSender from "./websocket/WsSender";
import type { WsState } from "./useWebSocketConnection";

interface AppState {
  kerutaState: KerutaTaskState;
  taskService: TaskService;
  queueService: QueueService;
  providerService: ProviderService;
}

const AppContext = createContext<AppState | null>(null);

export function AppProvider({ children }: { children: ReactNode }) {
  const [kerutaState, setKerutaState] = useState<KerutaTaskState>({ state: "unloaded" });
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

  // KerutaTaskState management
  useEffect(() => {
    if (wsState.state === "open" && kerutaState.state !== "connected") {
      setKerutaState({ state: "connected", auth: { state: "unauthenticated" } });
    } else if (wsState.state === "closed") {
      setKerutaState({ state: "disconnected" });
    } else if (wsState.state !== "open" && kerutaState.state === "connected") {
      setKerutaState({ state: "unloaded" });
    }
  }, [wsState.state, kerutaState.state]);

  // Message handling - route messages to domain services
  useEffect(() => {
    if (wsState.state !== "open" || kerutaState.state !== "connected") return;
    const ws = wsState.websocket;

    const handler = (event: MessageEvent) => {
      const msg = JSON.parse(event.data);
      services.taskService.handleMessage(msg);
      services.queueService.handleMessage(msg);
      services.providerService.handleMessage(msg);
    };

    ws.addEventListener("message", handler);
    return () => ws.removeEventListener("message", handler);
  }, [wsState, kerutaState.state, services]);

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

export type KerutaTaskState =
  | { state: "unloaded" }
  | ConnectedKerutaTaskState
  | { state: "disconnected" };

export interface ConnectedKerutaTaskState {
  state: "connected";
  auth: AuthState;
}

export type AuthState = { state: "unauthenticated" } | { state: "authenticated" };
