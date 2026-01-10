import {createContext, Dispatch, ReactNode, SetStateAction, useCallback, useContext, useEffect, useState} from "react";


const Context = createContext<WebsocketState>({state: "unloaded"});

export function WebsocketProvider(
    {
        wsUrl,
        children,
        ...props
    }: {
        wsUrl: URL,
        children: ReactNode
    }) {
    const [wsState, setWsState] = useState<WebsocketState>({
        state: "unloaded",
    })
    const [onMsg, setOnMsg] = useState<((event: MessageEvent) => void)>((e) => {
        console.log("websocket message handler not set")
    })
    const {open, close, err} = useHandlers(setWsState, setOnMsg)
    useEffect(() => {
        if (wsState.state != "unloaded") return
        const websocket = new WebSocket(wsUrl)
        const newState: WebsocketState = {
            state: "loaded",
            websocket: websocket,
            setOnMsg
        }
        setWsState(newState)
    }, [wsState.state]);

    useEffect(() => {
        if (wsState.state == "unloaded") return
        wsState.websocket.addEventListener("open", open);
        wsState.websocket.addEventListener("message", onMsg);
        wsState.websocket.addEventListener("error", err);
        wsState.websocket.addEventListener("close", close);
        return () => {
            wsState.websocket.removeEventListener("open", open);
            wsState.websocket.removeEventListener("message", onMsg);
            wsState.websocket.removeEventListener("error", err);
            wsState.websocket.removeEventListener("close", close);
        }
    }, [wsState, open, onMsg, err, close]);

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
    state: "unloaded",
} | {
    state: "loaded",
    websocket: WebSocket,
    setOnMsg: Dispatch<SetStateAction<(event: MessageEvent) => void>>,
} | WebsocketOpenState | {
    state: "closed",
    websocket: WebSocket,
    setOnMsg: Dispatch<SetStateAction<(event: MessageEvent) => void>>,
    open(): void,
}

export interface WebsocketOpenState {
    state: "open",
    websocket: WebSocket,
    close: () => void,
    setOnMsg: Dispatch<SetStateAction<(event: MessageEvent) => void>>,
}

function useHandlers(
    setWsState: Dispatch<SetStateAction<WebsocketState>>,
    setOnMsg: Dispatch<SetStateAction<() => void>>,
) {
    const err = useCallback(() => {
        console.log("websocket error")
        setWsState(prevState => {
            if (prevState.state != "loaded") return prevState
            return {
                state: "unloaded"
            }
        })
    }, []);
    const open = useCallback(() => {
        console.log("websocket opened")
        setWsState(prevState => {
            if (prevState.state != "loaded") return prevState
            return {
                state: "open",
                websocket: prevState.websocket,
                close() {
                    prevState.websocket.close()
                },
                setOnMsg
            }
        })
    }, [])
    const close = useCallback(() => {
        setWsState(prevState => {
            if (prevState.state != "open") return prevState
            return {
                state: "closed",
                websocket: prevState.websocket,
                open() {
                    setWsState(prevState1 => {
                        if (prevState1.state != "closed") return prevState1
                        return {
                            state: "unloaded"
                        }
                    })
                },
                setOnMsg
            }
        })
    }, []);
    return {err, open, close}
}
