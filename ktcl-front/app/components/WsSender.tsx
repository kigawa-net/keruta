import {useWebsocketState, WebsocketState} from "./Websocket";
import {KerutaTaskState, useKerutaTaskState} from "./KerutaTask";
import {KeycloakState, useKeycloakState} from "./Keycloak";
import {AuthRequestMsg} from "../msg/auth";
import {useEffect} from "react";

export default function WsSender(
    {}: {},
) {
    console.log("rendering ws sender")
    const wsState = useWebsocketState()
    console.log(wsState)
    const kc = useKeycloakState()
    console.log(kc)
    const kerutaState = useKerutaTaskState()
    console.log(kerutaState)
    console.log(kerutaState.state == "connected" ? kerutaState.auth : undefined)
    useAuth(wsState, kc, kerutaState)
    useRetry(wsState)
    return undefined
}


function useAuth(
    wsState: WebsocketState, kc: KeycloakState, kerutaState: KerutaTaskState
) {
    useEffect(() => {
        if (!(
            wsState.state == "open"
            && kc.state == "authenticated"
            && kerutaState.state == "connected"
            && kerutaState.auth.state == "unauthenticated"
        )) return;
        const f = ()=>{
            kc.getToken().then(value => {
                const msg: AuthRequestMsg = {
                    type: "auth_request",
                    token: value,
                }
                wsState.websocket.send(JSON.stringify(msg))
            })
        }
        const t = setInterval(() => {
            f()
        }, 1000 * 5)
        f()
        return () => clearInterval(t)
    }, [
        wsState.state == "open"
        && kc.state == "authenticated"
        && kerutaState.state == "connected"
        && kerutaState.auth.state == "unauthenticated"
    ]);
}

function useRetry(
    wsState: WebsocketState
) {
    useEffect(() => {
        if (!(
            wsState.state == "closed"
        )) return;
        const timeout = setTimeout(() => wsState.open(), 1000 * 5)
        return () => clearTimeout(timeout)
    }, [
        wsState.state == "closed"
    ]);
}
