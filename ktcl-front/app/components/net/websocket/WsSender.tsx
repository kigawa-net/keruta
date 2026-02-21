import {useEffect} from "react";
import {useAuthMessageService, useTokenApiService,} from "../../service/useServiceHooks";
import {KeycloakState, useKeycloakState} from "../../auth/Keycloak";
import {useKerutaTaskState} from "../../app/useAppState";
import {GlobalState, useGlobalState} from "../../app/Global";
import {KerutaTaskState} from "./ConnectionStateTypes";

export default function WsSender() {
    const globalState = useGlobalState();
    const kc = useKeycloakState();
    const kerutaState = useKerutaTaskState();
    useAuth(globalState, kc, kerutaState);
    useRetry(globalState);
    return null;
}

function useAuth(globalState: GlobalState, kc: KeycloakState, kerutaState: KerutaTaskState) {
    const authMsgService = useAuthMessageService();
    const tokenApiService = useTokenApiService();

    useEffect(() => {
        const shouldAuth =
            globalState.state === "open" &&
            kc.state === "authenticated" &&
            kerutaState.state === "connected" &&
            kerutaState.auth.state === "unauthenticated";
        if (!shouldAuth) return;

        const doAuth = async () => {
            if (globalState.state !== "open") return;
            try {
                const userToken = await kc.getToken();
                const result = await tokenApiService.getServerToken(userToken);
                console.debug("Auth request result:", result);
                // Check again after async operations - WebSocket may have closed during await
                if (globalState.state !== "open") {
                    console.debug("Auth request failed: WebSocket is closed");
                    return;
                }
                if (result.success) {
                    authMsgService.sendAuthRequest(userToken, result.data.token);
                } else {
                    console.debug("Auth request failed: token server returned failure");
                }
            } catch (e) {
                console.warn("Auth request failed:", e);
            }
        };

        const interval = setInterval(doAuth, 5000);
        doAuth();
        return () => clearInterval(interval);
    }, [globalState.state, kc, kerutaState, authMsgService, tokenApiService]);
}

function useRetry(globalState: GlobalState) {
    useEffect(() => {
        if (globalState.state === "closed") {
            // const timeout = setTimeout(() => globalState.open(), 5000);
            // return () => clearTimeout(timeout);
        }
        if (globalState.state === "error") {
            // const timeout = setTimeout(() => globalState.retry(), 10000);
            // return () => clearTimeout(timeout);
        }
    }, [globalState]);
}
