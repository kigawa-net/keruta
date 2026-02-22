import {createContext, ReactNode, useMemo} from "react";
import {ProviderService, QueueService, TaskService} from "../domain";
import {useConnectionStateService} from "../../util/net/websocket/useConnectionStateService";

import {
    useProviderMessageService,
    useQueueMessageService,
    useTaskMessageService,
} from "../../util/net/service/useServiceHooks";
import type {KerutaTaskState} from "../../util/net/websocket/ConnectionStateTypes";
import {useWebsocketState} from "../../util/net/websocket/WebsocketProvider";



export interface AppState {
    kerutaState: KerutaTaskState;
    taskService: TaskService;
    queueService: QueueService;
    providerService: ProviderService;
}

export const AppContentContext = createContext<AppState | null>(null);

export function AppContentProvider({children}: { children: ReactNode }) {
    const websocketState = useWebsocketState();
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
    const {kerutaState, setAuthState} = useConnectionStateService(websocketState);

    const appState: AppState = {
        kerutaState,
        taskService: services.taskService,
        queueService: services.queueService,
        providerService: services.providerService,
    };

    return (
        <AppContentContext.Provider value={appState}>
            {children}
        </AppContentContext.Provider>
    );
}


// Re-export types for convenience
export type {KerutaTaskState, ConnectedKerutaTaskState, AuthState} from "../../util/net/websocket/ConnectionStateTypes";
