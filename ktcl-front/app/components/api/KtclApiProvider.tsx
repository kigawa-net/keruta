import {createContext, ReactNode, useContext, useEffect, useState} from "react";
import {KtclApi} from "./KtclApi";

const Context = createContext<KtclApiState>({state: "unloaded"});

export function KtclApiProvider(
    {
        ...props
    }: {
        children?: ReactNode
    }
) {
    const [ApiClientState, setApiClientState] = useState<KtclApiState>({state: "unloaded"});
    useEffect(() => {
        setApiClientState({
            state: "loaded",
            ktclApi: new KtclApi(null)
        })
    }, []);
    return <Context.Provider
        value={ApiClientState}
        {...props}
    >
    </Context.Provider>;
}

export function useKtclApiState() {
    return useContext(Context);
}

export type KtclApiState = { state: "unloaded" } | { state: "loaded", ktclApi: KtclApi }
