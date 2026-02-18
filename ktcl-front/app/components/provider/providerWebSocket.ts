import { useCallback } from "react";
import { WsState } from "../ServiceContext";
import useWsReceive from "../websocket/useWsReceive";
import { ClientProviderAddTokenMsg } from "../../msg/provider";

export type ProviderAddCallback = (msg: ClientProviderAddTokenMsg) => void;

export function useProviderAddWebSocket(
    wsState: WsState,
    onTokenReceived: ProviderAddCallback,
) {
    const handleReceive = useCallback((msg: {type: string}) => {
        if (msg.type === "provider_add_token_issued") {
            onTokenReceived(msg as ClientProviderAddTokenMsg);
        }
    }, [onTokenReceived]);

    useWsReceive(wsState, handleReceive, [handleReceive]);
}

export function sendProviderAddMessage(
    wsState: WsState,
    message: {type: string; name: string; issuer: string; audience: string},
): void {
    if (wsState.state !== "open") {
        throw new Error("WebSocketが接続されていません");
    }
    wsState.websocket.send(JSON.stringify(message));
}
