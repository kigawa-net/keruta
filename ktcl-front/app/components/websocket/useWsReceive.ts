import {WsState} from "./Websocket";
import {useCallback, useEffect} from "react";
import {ReceiveMsg} from "../../msg/msg";

export default function useWsReceive(
  wsState: WsState, onReceive: (msg: ReceiveMsg) => void, deps: any[]
) {
    const onEvt = useCallback((event: MessageEvent) => {
        if (event == undefined) return
        const message = JSON.parse(event.data) as ReceiveMsg
        onReceive(message)
    }, deps);
    useEffect(() => {
        if (wsState.state != "open") return
        wsState.websocket.addEventListener("message", onEvt)
        return () => wsState.websocket.removeEventListener("message", onEvt)
    }, [wsState.state, onEvt]);
}
