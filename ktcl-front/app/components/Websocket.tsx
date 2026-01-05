import {createContext, ReactNode, useContext, useEffect, useMemo, useState} from "react";


const Context = createContext<WebsocketState>({state: "unloaded"});

export function WebsocketProvider(
    {
        wsUrl,
        onOpen,
        onMsg,
        onErr,
        onClose,
        children,
        ...props
    }: {
        wsUrl: URL,
        onOpen: () => void,
        onMsg: (msg: MessageEvent) => void,
        onErr: () => void,
        onClose: () => void,
        children: ReactNode
    }) {
    const [ws, setWs] = useState<WebSocket | undefined>()

    useEffect(() => {
        setWs(prevState => {
            if (prevState != undefined) return prevState
            const websocket = new WebSocket(wsUrl)
            websocket.onopen = onOpen;
            websocket.onmessage = onMsg;
            websocket.onerror = onErr;
            websocket.onclose = onClose;
            return websocket
        })
    }, []);

    useEffect(() => {
        if (ws == undefined) return
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
        {children}
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
