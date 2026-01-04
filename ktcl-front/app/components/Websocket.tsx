import {createContext, useContext, useEffect, useMemo, useState} from "react";


const Context = createContext<WebsocketState>({state: "unloaded"});

export function WebsocketProvider(
    {
        wsUrl,
        onOpen,
        onMsg,
        onErr,
        onClose,
        ...props
    }: {
        wsUrl: URL,
        onOpen: () => void,
        onMsg: (msg: MessageEvent) => void,
        onErr: () => void,
        onClose: () => void
    }) {
    const [ws, setWs] = useState<WebSocket>()

    useEffect(() => {
        setWs(prevState => prevState ?? new WebSocket(wsUrl))
    }, []);

    useEffect(() => {
        ws.onopen = onOpen;
        ws.onmessage = onMsg;
        ws.onerror = onErr;
        ws.onclose = onClose;
    }, [ws, onOpen, onMsg, onErr, onClose]);
    const wsState = useMemo<WebsocketState>(() => {
        if (ws == undefined) return {state: "unloaded"}
        return {
            state: "loaded",
            websocket: ws
        }
    }, [ws])
    return <Context.Provider
        value={wsState}
        {...props}
    >
    </Context.Provider>;
}

export function useWebsocketState() {
    return useContext(Context);
}

export type WebsocketState = {
    state: "unloaded"
} | {
    state: "loaded",
    websocket: WebSocket
}
