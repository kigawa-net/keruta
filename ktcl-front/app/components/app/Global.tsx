import {createContext, Dispatch, ReactNode, SetStateAction, useContext, useState} from "react";
import {useWebSocket} from "../net/websocket/hooks";
import {Url} from "../net/Url";

const Context = createContext<GlobalState>({state: "unloaded"});

export function GlobalProvider(
    {
        wsUrl,
        ...parent
    }: { wsUrl: Url, children?: ReactNode }
) {
    const [GlobalState, setGlobalState] = useState<GlobalState>({state: "unloaded"});
    useWebSocket(wsUrl, setGlobalState)
    console.debug("GlobalState", GlobalState)
    return <Context.Provider
        value={GlobalState}
        {...parent}
    >
    </Context.Provider>;
}

export function useGlobalState() {
    return useContext(Context);
}

export type GlobalState = {
    state: "unloaded"
} | {
    state: "open"
    websocket: WebSocket
    set: Dispatch<SetStateAction<GlobalState>>
} | {
    state: "loaded"
    websocket: WebSocket
    set: Dispatch<SetStateAction<GlobalState>>
} | {
    state: "closed"
    set: Dispatch<SetStateAction<GlobalState>>
} | {
    state: "error"
    set: Dispatch<SetStateAction<GlobalState>>
}
