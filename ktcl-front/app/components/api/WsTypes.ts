/**
 * WebSocketの接続状態
 */
export type WsConnectionState =
  | "disconnected"
  | "connecting"
  | "connected"
  | "error";

/**
 * WebSocket接続のオプション
 */
export interface WsConnectionOptions {
  url: URL;
  onOpen?: () => void;
  onClose?: () => void;
  onError?: (error: Event) => void;
  onMessage?: (message: unknown) => void;
}

/**
 * メッセージハンドラの型定義
 */
export type MessageHandler<T = unknown> = (message: T) => void;
