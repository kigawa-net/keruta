import {createContext, ReactNode, useContext, useEffect, useState} from "react";
import {KtseApi} from "./KtseApi";
import {useWebsocketState} from "../../util/net/websocket/WebsocketProvider";

const Context = createContext<KtseApiState>({state: "unloaded"});

export function KtseApiProvider(
    {
        ...props
    }: {
        children: ReactNode
    }
) {
    const [KtseApiState, setKtseApiState] = useState<KtseApiState>({state: "unloaded"});
    const websocket = useWebsocketState()
    useEffect(() => {
        if (websocket.state != "open") return;
        setKtseApiState({state: "loaded", ktclApi: new KtseApi(websocket.websocket)})
    }, [websocket]);

    return <Context.Provider
        value={KtseApiState}
        {...props}
    >
    </Context.Provider>;
}

export function useKtseApiState() {
    return useContext(Context);
}

export type KtseApiState = { state: "unloaded" } | { state: "loaded", ktclApi: KtseApi }
