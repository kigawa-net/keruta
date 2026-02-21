import {Dispatch, SetStateAction, useEffect} from "react";
import {Ws} from "./Ws";
import {Url} from "../Url";
import {GlobalState} from "../../app/Global";

export function useWebSocket(url: Url, setGlobalState: Dispatch<SetStateAction<GlobalState>>) {
    useEffect(() => {
        return websocketEffect(url, setGlobalState)
    }, [url]);
}

function websocketEffect(url: Url, setGlobalState: Dispatch<SetStateAction<GlobalState>>) {
    const ws = Ws.connect(url)
    ws.addEventListener("close", () => {
        handleClose(setGlobalState, ws)
    })
    ws.addEventListener("error", () => {
        handleError(setGlobalState, ws)
    })
    ws.addEventListener("open", () => {
        handleOpen(setGlobalState, ws)
    })
    setGlobalState({state: "loaded", websocket: ws, set: setGlobalState})
    return () => {
        if (ws.readyState === WebSocket.OPEN) ws.close()
    }
}

function handleOpen(setGlobalState: Dispatch<SetStateAction<GlobalState>>, ws: WebSocket) {
    setGlobalState(prevState => {
        if (prevState.state !== "loaded") return prevState
        if (prevState.websocket !== ws) return prevState
        return {state: "open", set: setGlobalState, websocket: ws}
    })
}

function handleError(setGlobalState: Dispatch<SetStateAction<GlobalState>>, ws: WebSocket) {
    setGlobalState(prevState => {
        if (prevState.state !== "loaded" && prevState.state !== "open") return prevState
        if (prevState.websocket !== ws) return prevState
        return {state: "error", set: setGlobalState}
    })
}

function handleClose(setGlobalState: Dispatch<SetStateAction<GlobalState>>, ws: WebSocket) {
    setGlobalState(prevState => {
        if (prevState.state !== "loaded" && prevState.state !== "open") {
            ws.close()
            return prevState
        }
        if (prevState.websocket !== ws) {
            ws.close()
            return prevState
        }
        return {state: "closed", set: setGlobalState}
    })
}
