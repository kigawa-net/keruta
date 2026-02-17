import {useWsState, WsState} from "./Websocket";
import {KerutaTaskState, useKerutaTaskState} from "../KerutaTask";
import {KeycloakState, useKeycloakState} from "../Keycloak";
import {ServerAuthRequestMsg} from "../../msg/auth";
import {useEffect} from "react";

export default function WsSender(
    {}: {},
) {
    const wsState = useWsState()
    const kc = useKeycloakState()
    const kerutaState = useKerutaTaskState()
    useAuth(wsState, kc, kerutaState)
    useRetry(wsState)
    return undefined
}


function useAuth(
  wsState: WsState, kc: KeycloakState, kerutaState: KerutaTaskState
) {
    useEffect(() => {
        if (!(
            wsState.state == "open"
            && kc.state == "authenticated"
            && kerutaState.state == "connected"
            && kerutaState.auth.state == "unauthenticated"
        )) return;
        const f = () => {
            Promise.all([
                kc.getToken().then(value =>
                    fetch("/api/token", {method: "POST", body: JSON.stringify({token: value})})
                ).then(value => value.json()),
                kc.getToken(),
            ]).then(([tokenRes, userToken]) => {
                    const token: { token: string } = tokenRes
                    const msg: ServerAuthRequestMsg = {
                        type: "auth_request",
                        userToken,
                        serverToken: token.token
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
    wsState: WsState
) {
    useEffect(() => {
        if (wsState.state == "closed") {
            const timeout = setTimeout(() => wsState.open(), 1000 * 5)
            return () => clearTimeout(timeout)
        }
        if (wsState.state == "error") {
            const timeout = setTimeout(() => wsState.retry(), 1000 * 10)
            return () => clearTimeout(timeout)
        }
    }, [wsState.state]);
}
