import { Dispatch, SetStateAction, useCallback } from "react";
import { WsState } from "./WsTypes";

export function useWsHandlers(setWsState: Dispatch<SetStateAction<WsState>>) {
  const err = useCallback(() => {
    console.log("websocket error");
    setWsState((prev) => {
      if (prev.state !== "loaded" && prev.state !== "open") return prev;
      return {
        state: "error",
        websocket: prev.websocket,
        retry() {
          setWsState({ state: "unloaded" });
        },
      };
    });
  }, [setWsState]);

  const open = useCallback(() => {
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
  }, [setWsState]);

  const close = useCallback(() => {
    setWsState((prev) => {
      if (prev.state !== "open") return prev;
      return {
        state: "closed",
        websocket: prev.websocket,
        open() {
          setWsState((p) => (p.state === "closed" ? { state: "unloaded" } : p));
        },
      };
    });
  }, [setWsState]);

  return { err, open, close };
}
