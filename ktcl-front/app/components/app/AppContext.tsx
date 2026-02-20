import {createContext, ReactNode, useCallback, useMemo} from "react";
import {ProviderService, QueueService, TaskService} from "../domain";
import {useConnectionStateService} from "../net/websocket/useConnectionStateService";
import {useMessageRouterService} from "../msg/useMessageRouterService";
import {
    useProviderMessageService,
    useQueueMessageService,
    useTaskMessageService,
    useWsState,
} from "../service/useServiceHooks";
import WsSender from "../net/websocket/WsSender";
import type {KerutaTaskState} from "../net/websocket/ConnectionStateTypes";

export interface AppState {
    kerutaState: KerutaTaskState;
    taskService: TaskService;
    queueService: QueueService;
    providerService: ProviderService;
}

export const AppContext = createContext<AppState | null>(null);

export function AppProvider({children}: { children: ReactNode }) {
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
    const {kerutaState, setAuthState} = useConnectionStateService(wsState);

    // Auth success callback
    const onAuthSuccess = useCallback(() => {
        setAuthState({state: "authenticated"});
    }, [setAuthState]);

    // Message routing
    useMessageRouterService({
        wsState,
        kerutaState,
        taskService: services.taskService,
        queueService: services.queueService,
        providerService: services.providerService,
        onAuthSuccess,
    });

    const appState: AppState = {
        kerutaState,
        taskService: services.taskService,
        queueService: services.queueService,
        providerService: services.providerService,
    };

    return (
        <AppContext.Provider value={appState}>
            <WsSender/>
            {children}
        </AppContext.Provider>
    );
}


// Re-export types for convenience
export type {KerutaTaskState, ConnectedKerutaTaskState, AuthState} from "../net/websocket/ConnectionStateTypes";
