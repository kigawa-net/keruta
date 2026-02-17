import {createContext, Dispatch, ReactNode, SetStateAction, useContext, useEffect, useState,} from "react";
import {WsState} from "./ServiceContext";
import WsSender from "./websocket/WsSender";
import {useDomainServices} from "./DomainContext";
import {useWsState} from "./useServiceHooks";

const Context = createContext<KerutaTaskState>({state: "unloaded"});

export function KerutaTaskProvider({children}: { children: ReactNode }) {
    const [state, setState] = useState<KerutaTaskState>({state: "unloaded"});
    const wsState = useWsState();
    const domainServices = useDomainServices();

    useLoad(wsState, state, setState);
    useMessageHandling(state, domainServices);

    return (
        <Context.Provider value={state}>
            <WsSender/>
            {children}
        </Context.Provider>
    );
}

function useLoad(
    wsState: WsState,
    kerutaState: KerutaTaskState,
    setState: Dispatch<SetStateAction<KerutaTaskState>>
) {
    useEffect(() => {
        if (wsState.state === "open" && kerutaState.state !== "connected") {
            setState({state: "connected", auth: {state: "unauthenticated"}});
        } else if (wsState.state === "closed") {
            setState({state: "disconnected"});
        } else if (wsState.state !== "open" && kerutaState.state === "connected") {
            setState({state: "unloaded"});
        }
    }, [wsState.state, kerutaState.state]);
}

function useMessageHandling(
    state: KerutaTaskState,
    domainServices: ReturnType<typeof useDomainServices>
) {
    const wsState = useWsState();

    useEffect(() => {
        if (wsState.state !== "open" || state.state !== "connected") return;
        const ws = wsState.websocket;

        const handler = (event: MessageEvent) => {
            const msg = JSON.parse(event.data);
            if (msg.type === "auth_success" && state.state === "connected") {
                // auth success handled in state
            }
            domainServices.taskService.handleMessage(msg);
            domainServices.queueService.handleMessage(msg);
            domainServices.providerService.handleMessage(msg);
        };

        ws.addEventListener("message", handler);
        return () => ws.removeEventListener("message", handler);
    }, [wsState, state, domainServices]);
}

export function useKerutaTaskState() {
    return useContext(Context);
}

export type KerutaTaskState =
    | { state: "unloaded" }
    | ConnectedKerutaTaskState
    | { state: "disconnected" };

export interface ConnectedKerutaTaskState {
    state: "connected";
    auth: AuthState;
}

export type AuthState = { state: "unauthenticated" } | { state: "authenticated" };
