import {WebsocketState} from "../components/Websocket";
import {useEffect} from "react";

export default function useWsSender(wsState: WebsocketState) {
    useEffect(() => {
        if (wsState.state != "loaded") return

    }, [wsState.state]);
}
