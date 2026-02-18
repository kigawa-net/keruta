import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { WsState } from "./WsTypes";
import { useWsHandlers } from "./WsHandlers";

const Context = createContext<WsState>({ state: "unloaded" });

export function WebsocketProvider({
  wsUrl,
  children,
}: {
  wsUrl: URL;
  children: ReactNode;
}) {
  const [wsState, setWsState] = useState<WsState>({ state: "unloaded" });
  const { open, close, err } = useWsHandlers(setWsState);

  useEffect(() => {
    if (wsState.state !== "unloaded") return;
    setWsState({ state: "loaded", websocket: new WebSocket(wsUrl) });
  }, [wsState.state, wsUrl]);

  useEffect(() => {
    if (wsState.state === "unloaded") return;
    const ws = wsState.websocket;
    ws.addEventListener("open", open);
    ws.addEventListener("error", err);
    ws.addEventListener("close", close);
    return () => {
      ws.removeEventListener("open", open);
      ws.removeEventListener("error", err);
      ws.removeEventListener("close", close);
    };
  }, [wsState, open, err, close]);

  return <Context.Provider value={wsState}>{children}</Context.Provider>;
}

export function useWsState() {
  return useContext(Context);
}

export type { WsState, WebsocketOpenState } from "./WsTypes";
