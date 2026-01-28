// noinspection JSUnusedGlobalSymbols
import useWsReceive from "../components/websocket/useWsReceive";
import {useWsState} from "../components/websocket/Websocket";
import {useEffect, useState} from "react";
import {ClientProviderListMsg, ServerProvidersRequestMsg} from "../msg/provider";
import {useKerutaTaskState} from "../components/KerutaTask";

type Provider = ClientProviderListMsg["providers"][0]
export default function AboutRoute() {
    const wsState = useWsState()
    const [providers, setProviders] = useState<Provider[]>()
    const kerutaState = useKerutaTaskState()
    useWsReceive(wsState, msg => {
        if (msg.type != "provider_list") return
        setProviders(msg.providers)
    }, [])
    useEffect(() => {
        if (wsState.state != "open") return
        if (kerutaState.state != "connected") return;
        if (kerutaState.auth.state != "authenticated") return;
        const msg: ServerProvidersRequestMsg = {
            type: "providers_request"
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [wsState.state, kerutaState.state == "connected" && kerutaState.auth.state]);
    return <div>{
        providers?.map(p => <div key={p.id}>{p.name}</div>)
    }</div>
}
