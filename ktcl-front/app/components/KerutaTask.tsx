import {createContext, Dispatch, type ReactNode, SetStateAction, useContext, useEffect, useState} from "react";
import {useWebsocketState, WebsocketState} from "./websocket/Websocket";
import WsSender from "./websocket/WsSender";
import {ReceiveMsg} from "../msg/msg";
import useWsReceive from "./websocket/useWsReceive";


const Context = createContext<KerutaTaskState>({state: "unloaded"});

export function KerutaTaskProvider(
    {
        children,
        ...props
    }: {
        children: ReactNode
    }) {
    const [KerutaTaskState, setKerutaTaskState] = useState<KerutaTaskState>({state: "unloaded"});
    const wsState = useWebsocketState()
    useLoad(wsState, KerutaTaskState, setKerutaTaskState)
    useWsReceive(wsState, (msg) => {
        if (KerutaTaskState.state != "connected") return
        onMsg(msg, KerutaTaskState, setKerutaTaskState)
    }, [KerutaTaskState])
    return <Context.Provider
        value={KerutaTaskState}
        {...props}
    >
        <WsSender/>
        {children}
    </Context.Provider>;
}


function useLoad(
    wsState: WebsocketState,
    kerutaState: KerutaTaskState,
    setKerutaTaskState: Dispatch<SetStateAction<KerutaTaskState>>
) {
    useEffect(() => {
        if (wsState.state == "open") {
            if (kerutaState.state == "connected") return
            setKerutaTaskState({
                state: "connected",
                auth: {state: "unauthenticated"},
            })
        } else if (wsState.state == "closed") {
            setKerutaTaskState({state: "disconnected"})
        } else {
            setKerutaTaskState({state: "unloaded"})
        }
    }, [
        wsState.state,
        kerutaState.state
    ]);
}

export function useKerutaTaskState() {
    return useContext(Context);
}

export type KerutaTaskState = {
    state: "unloaded"
} | ConnectedKerutaTaskState | {
    state: "disconnected"
}

export interface ConnectedKerutaTaskState {
    state: "connected",
    auth: AuthState
}

export type AuthState = {
    state: "unauthenticated"
} | {
    state: "authenticated"
}

function onMsg(
    msg: ReceiveMsg, kerutaState: ConnectedKerutaTaskState,
    setKerutaTaskState: Dispatch<SetStateAction<KerutaTaskState>>
) {
    switch (msg.type) {
        case "auth_success": {
            setKerutaTaskState({...kerutaState, auth: {state: "authenticated"}})
        }
    }
}
