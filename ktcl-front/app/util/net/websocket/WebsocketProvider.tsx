import {createContext, Dispatch, ReactNode, SetStateAction, useContext, useEffect, useState} from "react";
import {Url} from "../Url";
import {Ws} from "./Ws";

const Context = createContext<WebsocketState>({state: "unloaded"});

export function WebsocketProvider(
    {
        wsUrl,
        ...parent
    }: { wsUrl: Url, children?: ReactNode }
) {
    const [WebsocketState, setWebsocketState] = useState<WebsocketState>({state: "unloaded"});

    useEffect(() => {
        const ws = Ws.connect(wsUrl)
        ws.addEventListener("close", () => {
            handleClose(setWebsocketState, ws)
        })
        ws.addEventListener("error", () => {
            handleError(setWebsocketState, ws)
        })
        ws.addEventListener("open", () => {
            handleOpen(setWebsocketState, ws)
        })
        setWebsocketState({state: "loaded", websocket: ws, set: setWebsocketState})
        return () => {
            if (ws.readyState === WebSocket.OPEN) ws.close()
        }
    }, [wsUrl]);
    return <Context.Provider
        value={WebsocketState}
        {...parent}
    >
        {parent.children}
    </Context.Provider>;
}

export function useWebsocketState() {
    return useContext(Context);
}

export type WebsocketState = {
    state: "unloaded"
} | {
    state: "open"
    websocket: WebSocket
    set: Dispatch<SetStateAction<WebsocketState>>
} | {
    state: "loaded"
    websocket: WebSocket
    set: Dispatch<SetStateAction<WebsocketState>>
} | {
    state: "closed"
    set: Dispatch<SetStateAction<WebsocketState>>
} | {
    state: "error"
    set: Dispatch<SetStateAction<WebsocketState>>
}


function handleOpen(setGlobalState: Dispatch<SetStateAction<WebsocketState>>, ws: WebSocket) {
    setGlobalState(prevState => {
        if (prevState.state !== "loaded") return prevState
        if (prevState.websocket !== ws) return prevState
        return {websocket: ws, state: "open", set: prevState.set}
    })
}

function handleError(setGlobalState: Dispatch<SetStateAction<WebsocketState>>, ws: WebSocket) {
    setGlobalState(prevState => {
        if (prevState.state !== "loaded" && prevState.state !== "open") return prevState
        if (prevState.websocket !== ws) return prevState
        return {state: "error", set: prevState.set}
    })
}

function handleClose(setGlobalState: Dispatch<SetStateAction<WebsocketState>>, ws: WebSocket) {
    setGlobalState(prevState => {
        if (prevState.state !== "loaded" && prevState.state !== "open") {
            ws.close()
            return prevState
        }
        if (prevState.websocket !== ws) {
            ws.close()
            return prevState
        }
        return {state: "closed", set: prevState.set}
    })
}
