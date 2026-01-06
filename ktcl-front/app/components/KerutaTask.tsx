import {createContext, Dispatch, type ReactNode, SetStateAction, useCallback, useContext, useState} from "react";
import {Config} from "../Config";
import {WebsocketProvider} from "./Websocket";


const Context = createContext<KerutaTaskState>({state: "unloaded"});

export function KerutaTaskProvider(
    {
        children,
        ...props
    }: {
        children: ReactNode
    }) {
    const [KerutaTaskState, setKerutaTaskState] = useState<KerutaTaskState>({state: "unloaded"});
    const close = useCallback(() => onClose(setKerutaTaskState), []);
    const open = useCallback(() => onOpen(setKerutaTaskState), []);
    const err = useCallback(() => onErr(setKerutaTaskState), []);
    const msg = useCallback(
        (msg: MessageEvent) => onMsg(msg, setKerutaTaskState), []
    );

    return <Context.Provider
        value={KerutaTaskState}
        {...props}
    >
        <WebsocketProvider
            onClose={close}
            onOpen={open}
            onErr={err}
            onMsg={msg}
            wsUrl={Config.websocketUrl}
        >
            {children}
        </WebsocketProvider>
    </Context.Provider>;
}

function onMsg(msg: MessageEvent, set: Dispatch<SetStateAction<KerutaTaskState>>) {
    console.log(msg)
}

function onErr(set: Dispatch<SetStateAction<KerutaTaskState>>) {
    set({state: "disconnected"})
}

function onOpen(set: Dispatch<SetStateAction<KerutaTaskState>>) {
    set({state: "connected"})
}

function onClose(set: Dispatch<SetStateAction<KerutaTaskState>>) {
    set({state: "disconnected"})
}

export function useKerutaTaskState() {
    return useContext(Context);
}

export type KerutaTaskState = {
    state: "unloaded"
} | {
    state: "connected"
} | {
    state: "disconnected"
}
