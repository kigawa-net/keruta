import {createContext, Dispatch, ReactNode, SetStateAction, useCallback, useContext, useEffect, useState} from "react";


const Context = createContext<WsState>({state: "unloaded"});

export function WebsocketProvider(
    {
        wsUrl,
        children,
        ...props
    }: {
        wsUrl: URL,
        children: ReactNode
    }) {
    const [wsState, setWsState] = useState<WsState>({
        state: "unloaded",
    })

    const {open, close, err} = useHandlers(setWsState)
    useEffect(() => {
        if (wsState.state != "unloaded") return
        const websocket = new WebSocket(wsUrl)
        const newState: WsState = {
            state: "loaded",
            websocket: websocket,
        }
        setWsState(newState)
    }, [wsState.state]);

    useEffect(() => {
        if (wsState.state == "unloaded") return
        wsState.websocket.addEventListener("open", open);
        wsState.websocket.addEventListener("error", err);
        wsState.websocket.addEventListener("close", close);
        return () => {
            wsState.websocket.removeEventListener("open", open);
            wsState.websocket.removeEventListener("error", err);
            wsState.websocket.removeEventListener("close", close);
        }
    }, [wsState.state, open, err, close]);

    return <Context.Provider
        value={wsState}
        {...props}
    >
        {children}
    </Context.Provider>;
}

export function useWsState() {
    return useContext(Context);
}

export type WsState = {
    state: "unloaded",
} | {
    state: "loaded",
    websocket: WebSocket,
} | WebsocketOpenState | {
    state: "closed",
    websocket: WebSocket,
    open(): void,
} | {
    state: "error",
    websocket: WebSocket,
    retry(): void,
}

export interface WebsocketOpenState {
    state: "open",
    websocket: WebSocket,
    close: () => void,
}

function useHandlers(
    setWsState: Dispatch<SetStateAction<WsState>>,
) {
    const err = useCallback(() => {
        console.log("websocket error")
        setWsState(prevState => {
            if (prevState.state != "loaded" && prevState.state != "open") return prevState
            return {
                state: "error",
                websocket: prevState.websocket,
                retry() {
                    setWsState({
                        state: "unloaded"
                    })
                },
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
            }
        })
    }, []);
    return {err, open, close}
}
