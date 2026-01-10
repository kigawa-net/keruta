import {createContext, Dispatch, type ReactNode, SetStateAction, useContext, useEffect, useState} from "react";
import {useWebsocketState, WebsocketState} from "./Websocket";
import WsSender from "./WsSender";
import {ReceiveMsg} from "../msg/msg";


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
    useEffect(() => {
        if (wsState.state == "unloaded") return
        if (KerutaTaskState.state != "connected") return
        wsState.setOnMsg(() => ((event: MessageEvent) => {
            console.log(event)
            if (event == undefined) return
            const message = JSON.parse(event.data) as ReceiveMsg
            onMsg(message, KerutaTaskState, setKerutaTaskState)
        }))
    }, [
        wsState.state, KerutaTaskState
    ]);
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
