import {createContext, ReactNode, useCallback, useMemo} from "react";
import {ProviderService, QueueService, TaskService} from "../domain";
import {useConnectionStateService} from "../net/websocket/useConnectionStateService";
import {useMessageRouterService} from "../msg/useMessageRouterService";
import {useProviderMessageService, useQueueMessageService, useTaskMessageService,} from "../service/useServiceHooks";
import WsSender from "../net/websocket/WsSender";
import type {KerutaTaskState} from "../net/websocket/ConnectionStateTypes";
import {useGlobalState} from "./Global";

export interface AppState {
    kerutaState: KerutaTaskState;
    taskService: TaskService;
    queueService: QueueService;
    providerService: ProviderService;
}

export const AppContentContext = createContext<AppState | null>(null);

export function AppContentProvider({children}: { children: ReactNode }) {
    const globalState = useGlobalState();
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
    const {kerutaState, setAuthState} = useConnectionStateService(globalState);

    // Auth success callback
    const onAuthSuccess = useCallback(() => {
        setAuthState({state: "authenticated"});
    }, [setAuthState]);

    // Message routing
    useMessageRouterService({
        globalState: globalState,
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
        <AppContentContext.Provider value={appState}>
            <WsSender/>
            {children}
        </AppContentContext.Provider>
    );
}


// Re-export types for convenience
export type {KerutaTaskState, ConnectedKerutaTaskState, AuthState} from "../net/websocket/ConnectionStateTypes";
