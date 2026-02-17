import { useEffect, useState } from "react";

export type WsState =
  | { state: "unloaded" }
  | { state: "loaded"; websocket: WebSocket }
  | WebsocketOpenState
  | { state: "closed"; websocket: WebSocket; open(): void }
  | { state: "error"; websocket: WebSocket; retry(): void };

export interface WebsocketOpenState {
  state: "open";
  websocket: WebSocket;
  close: () => void;
}

export function useWebSocketConnection(wsUrl: URL): WsState {
  const [wsState, setWsState] = useState<WsState>(() => ({
    state: "loaded",
    websocket: new WebSocket(wsUrl),
  }));

  // Handle WebSocket events
  useEffect(() => {
    if (wsState.state === "unloaded") return;
    const ws = wsState.websocket;

    const handleOpen = () => {
      console.log("websocket opened");
      setWsState((prev) => {
        if (prev.state !== "loaded") return prev;
        return {
          state: "open",
          websocket: prev.websocket,
          close() {
            prev.websocket.close();
          },
        };
      });
    };

    const handleError = () => {
      console.log("websocket error");
      setWsState((prev) => {
        if (prev.state !== "loaded" && prev.state !== "open") return prev;
        return {
          state: "error",
          websocket: prev.websocket,
          retry() {
            setWsState({ state: "loaded", websocket: new WebSocket(wsUrl) });
          },
        };
      });
    };

    const handleClose = () => {
      setWsState((prev) => {
        if (prev.state !== "open") return prev;
        return {
          state: "closed",
          websocket: prev.websocket,
          open() {
            setWsState({ state: "loaded", websocket: new WebSocket(wsUrl) });
          },
        };
      });
    };

    ws.addEventListener("open", handleOpen);
    ws.addEventListener("error", handleError);
    ws.addEventListener("close", handleClose);

    return () => {
      ws.removeEventListener("open", handleOpen);
      ws.removeEventListener("error", handleError);
      ws.removeEventListener("close", handleClose);
    };
  }, [wsState, wsUrl]);

  return wsState;
}
