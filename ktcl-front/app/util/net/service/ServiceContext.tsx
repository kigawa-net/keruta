import {createContext, ReactNode, useMemo} from "react";
import {ApiService, TaskMessageService, WebSocketService,} from "../../../components/api";
import {Url} from "../Url";
import {useWebsocketState, WebsocketState} from "../websocket/WebsocketProvider";

interface Services {
    wsService: WebSocketService;
    apiService: ApiService;
    taskMessageService: TaskMessageService;
}

export interface ServiceContextValue extends Services {
    globalState: WebsocketState;
}

export const ServiceContext = createContext<ServiceContextValue | null>(null);

export interface ServiceProviderProps {
    wsUrl: Url;
    apiBaseUrl?: string;
    children: ReactNode;
    getAuthToken?: () => Promise<string | null>;
}

export function ServiceProvider(
    {
        wsUrl,
        children,
        getAuthToken,
    }: ServiceProviderProps
) {
    const globalState = useWebsocketState();


    const services = useMemo(() => {
        const wsService = new WebSocketService({url: wsUrl});
        const apiService = new ApiService({
            getAuthToken,
        });
        return {
            wsService,
            apiService,
            taskMessageService: new TaskMessageService(wsService),
        };
    }, [wsUrl, getAuthToken]);

    const contextValue = useMemo<ServiceContextValue>(
        () => ({...services, globalState}),
        [services, globalState]
    );

    return (
        <ServiceContext.Provider value={contextValue}>{children}</ServiceContext.Provider>
    );
}

export type {WsState, WebsocketOpenState} from "../websocket/useWebSocketConnection";

