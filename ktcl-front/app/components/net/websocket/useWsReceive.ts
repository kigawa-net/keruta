import {useCallback, useEffect} from "react";
import {ReceiveMsg} from "../../msg/msg";
import {GlobalState} from "../../app/Global";

export default function useWsReceive(
    globalState: GlobalState, onReceive: (msg: ReceiveMsg) => void, deps: any[]
) {
    const onEvt = useCallback((event: MessageEvent) => {
        if (event == undefined) return
        const message = JSON.parse(event.data) as ReceiveMsg
        onReceive(message)
    }, deps);
    useEffect(() => {
        if (globalState.state != "open") return
        globalState.websocket.addEventListener("message", onEvt)
        return () => globalState.websocket.removeEventListener("message", onEvt)
    }, [globalState.state, onEvt]);
}
