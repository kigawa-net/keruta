import {createContext, useContext} from "react";
import {useWebSocket} from "../hooks/useWebSocket.ts";


const Context = createContext<WebsocketState | undefined>(undefined);

export function WebsocketProvider(
    {
        wsURL,
        onOpen,
        onMsg,
        onErr,
        onClose,
        ...props
    }: {
        wsURL: URL,
        onOpen: () => void,
        onMsg: (msg: MessageEvent) => void,
        onErr: () => void,
        onClose: () => void
    }) {
    const ws = useWebSocket(wsURL, onOpen, onMsg, onErr, onClose);

    return <Context.Provider
        value={ws}
        {...props}
    >
    </Context.Provider>;
}

export function useWebsocketState() {
    return useContext(Context);
}

export interface WebsocketState {
}
