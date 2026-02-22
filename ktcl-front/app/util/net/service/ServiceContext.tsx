import {createContext, ReactNode, useMemo} from "react";
import {
    ApiService,
    AuthMessageService,
    ProviderMessageService,
    QueueMessageService,
    TaskMessageService,
    WebSocketService,
} from "../../../components/api";
import {WsState} from "../websocket/useWebSocketConnection";
import {Url} from "../Url";
import {useWebsocketState, WebsocketState} from "../websocket/WebsocketProvider";

interface Services {
    wsService: WebSocketService;
    apiService: ApiService;
    authMessageService: AuthMessageService;
    taskMessageService: TaskMessageService;
    queueMessageService: QueueMessageService;
    providerMessageService: ProviderMessageService;
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
            authMessageService: new AuthMessageService(wsService),
            taskMessageService: new TaskMessageService(wsService),
            queueMessageService: new QueueMessageService(wsService),
            providerMessageService: new ProviderMessageService(wsService),
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

