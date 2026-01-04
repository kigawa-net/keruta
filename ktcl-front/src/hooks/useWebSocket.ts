import {useEffect, useMemo} from "react";
export function useWebSocket(
    wsUrl: URL,
    onOpen: () => void,
    onMsg: (msg: MessageEvent) => void,
    onErr: () => void,
    onClose: () => void,
): WebSocket {
    const ws = useMemo(() => {
        return new WebSocket(wsUrl);
    }, [wsUrl]);
    useEffect(() => {
        ws.onopen = onOpen;
        ws.onmessage = onMsg;
        ws.onerror = onErr;
        ws.onclose = onClose;
    }, [ws, onOpen, onMsg, onErr, onClose]);
    return ws
}
