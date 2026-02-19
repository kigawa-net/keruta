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
