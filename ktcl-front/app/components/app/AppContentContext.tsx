import {createContext, ReactNode, useMemo} from "react";
import {TaskService} from "../domain";
import {useConnectionStateService} from "../../util/net/websocket/useConnectionStateService";

import {useTaskMessageService,} from "../../util/net/service/useServiceHooks";
import type {KerutaTaskState} from "../../util/net/websocket/ConnectionStateTypes";
import {useWebsocketState} from "../../util/net/websocket/WebsocketProvider";


export interface AppState {
    kerutaState: KerutaTaskState;
    taskService: TaskService;
}

export const AppContentContext = createContext<AppState | null>(null);

export function AppContentProvider({children}: { children: ReactNode }) {
    const websocketState = useWebsocketState();
    const taskMsgService = useTaskMessageService();
// Domain services
    const services = useMemo(
        () => ({
            taskService: new TaskService(taskMsgService),
        }),
        [taskMsgService]
    );

    // Connection state management
    const {kerutaState} = useConnectionStateService(websocketState);

    const appState: AppState = {
        kerutaState,
        taskService: services.taskService,
    };

    return (
        <AppContentContext.Provider value={appState}>
            {children}
        </AppContentContext.Provider>
    );
}


// Re-export types for convenience
export type {KerutaTaskState, ConnectedKerutaTaskState, AuthState} from "../../util/net/websocket/ConnectionStateTypes";
