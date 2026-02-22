import {createContext, ReactNode, useContext, useEffect, useState} from "react";
import {useKtclApiState} from "./KtclApiProvider";
import {useKeycloakState} from "../auth/Keycloak";
import {useKtseApiState} from "./KtseApiProvider";
import {useStateFlow} from "../../util/StateFlow";
import {AuthedKtseApi} from "./AuthedKtseApi";

const Context = createContext<AuthedKtseState>({state: "unloaded"});

export function AuthedKtseProvider(
    {
        ...props
    }: {
        children: ReactNode
    }
) {
    const [AuthedKtseState, setAuthedKtseState] = useState<AuthedKtseState>({state: "unloaded"});
    const kc = useKeycloakState()
    const ktcl = useKtclApiState()
    const ktse = useKtseApiState()
    useEffect(() => {
        if (kc.state != "authenticated") return;
        if (ktcl.state != "loaded") return;
        if (ktse.state != "loaded") return;
        console.log("Sending auth request...")
        const userToken = kc.getToken()
        const serverToken = userToken.then(value => {
            return ktcl.ktclApi.getServerToken(value)
        })
        Promise.all([userToken, serverToken]).then(([userToken, serverToken]) => {
            ktse.ktclApi.auth.sendAuthRequest(userToken, serverToken.token)
        }).catch(reason => {
            console.error("Auth request failed:", reason)
        })
    }, [kc, ktcl, ktse]);
    useStateFlow(
        ktse.state == "loaded" ? ktse.ktclApi.auth.getAuthSuccessReceiver() : undefined,
        value => {
            console.log("Auth success:", value)
            setAuthedKtseState({state: "loaded", authedKtseApi: new AuthedKtseApi()})
        }
    )
    return <Context.Provider
        value={AuthedKtseState}
        {...props}
    >
    </Context.Provider>;
}

export function useAuthedKtseState() {
    return useContext(Context);
}

export type AuthedKtseState = { state: "unloaded" } | { state: "loaded", authedKtseApi: AuthedKtseApi }
