import {useEffect} from "react";
import {ReceiveMsg} from "../../../components/msg/msg";
import {useWebsocketState} from "./WebsocketProvider";



export function useWebsocketReceive(onReceive: (msg: ReceiveMsg) => void, deps: any[]) {
    const websocket = useWebsocketState()
    useEffect(() => {
        if (websocket.state !== "open") return
        return registerWebsocketReceiver(websocket.websocket, onReceive)
    }, [websocket,...deps]);
}

function registerWebsocketReceiver(websocket: WebSocket, onReceive: (msg: ReceiveMsg) => void) {
    function handler(msg: MessageEvent) {
        const message = JSON.parse(msg.data) as ReceiveMsg
        onReceive(message)
    }

    websocket.addEventListener("message", handler)
    return () => websocket.removeEventListener("message", handler)
}
