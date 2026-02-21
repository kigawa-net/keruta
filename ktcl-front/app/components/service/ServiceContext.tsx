import {createContext, ReactNode, useMemo} from "react";
import {
    ApiService,
    AuthMessageService,
    ProviderMessageService,
    QueueMessageService,
    TaskMessageService,
    TokenApiService,
    WebSocketService,
} from "../api";
import {WsState} from "../net/websocket/useWebSocketConnection";
import {Url} from "../net/Url";
import {GlobalState, useGlobalState} from "../app/Global";

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
    globalState: GlobalState;
}

export const ServiceContext = createContext<ServiceContextValue | null>(null);

export interface ServiceProviderProps {
    wsUrl: Url;
    apiBaseUrl?: string;
    children: ReactNode;
    getAuthToken?: () => Promise<string | null>;
    onUnauthorized?: () => void;
}

export function ServiceProvider(
    {
        wsUrl,
        children,
        getAuthToken,
        onUnauthorized,
    }: ServiceProviderProps
) {
    const globalState = useGlobalState();


    const services = useMemo(() => {
        const wsService = new WebSocketService({url: wsUrl});
        const apiService = new ApiService({
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
    }, [wsUrl, getAuthToken, onUnauthorized]);

    const contextValue = useMemo<ServiceContextValue>(
        () => ({...services, globalState}),
        [services, globalState]
    );

    return (
        <ServiceContext.Provider value={contextValue}>{children}</ServiceContext.Provider>
    );
}

export type {WsState, WebsocketOpenState} from "../net/websocket/useWebSocketConnection";

