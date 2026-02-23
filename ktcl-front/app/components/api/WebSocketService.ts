import {ReceiveMsg, SendMsgType} from "../msg/msg";
import {MessageHandler, WsConnectionOptions, WsConnectionState} from "./WsTypes";

/** WebSocket通信を抽象化するサービス */
export class WebSocketService {
    private websocket: WebSocket | null = null;
    private options: WsConnectionOptions;
    private connectionState: WsConnectionState = "disconnected";
    private messageHandlers: Map<string, MessageHandler[]> = new Map();
    private reconnectAttempts = 0;
    private maxReconnectAttempts = 5;
    private reconnectDelay = 1000;

    constructor(options: WsConnectionOptions) {
        this.options = options;
    }

    connect(): Promise<void> {
        return new Promise((resolve, reject) => {
            if (this.connectionState === "connected") {
                resolve();
                return;
            }
            this.connectionState = "connecting";
            this.websocket = new WebSocket(this.options.url.toJsURL());
            this.websocket.onopen = () => {
                this.connectionState = "connected";
                this.reconnectAttempts = 0;
                this.options.onOpen?.();
                resolve();
            };
            this.websocket.onclose = () => {
                this.connectionState = "disconnected";
                this.options.onClose?.();
            };
            this.websocket.onerror = (error) => {
                this.connectionState = "error";
                this.options.onError?.(error);
                reject(error);
            };
            this.websocket.onmessage = (event) => {
                try {
                    const message = JSON.parse(event.data) as ReceiveMsg;
                    this.handleMessage(message);
                    this.options.onMessage?.(message);
                } catch (e) {
                    console.error("Failed to parse WebSocket message:", e);
                }
            };
        });
    }

    disconnect(): void {
        if (this.websocket) {
            this.websocket.close();
            this.websocket = null;
            this.connectionState = "disconnected";
        }
    }

    async reconnect(): Promise<void> {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) throw new Error("Max reconnect attempts reached");
        this.reconnectAttempts++;
        const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
        await new Promise((resolve) => setTimeout(resolve, delay));
        return this.connect();
    }

    getState(): WsConnectionState {
        return this.connectionState;
    }

    isConnected(): boolean {
        return this.connectionState === "connected";
    }

    send<T extends { type: SendMsgType }>(message: T): void {
        if (!this.isConnected() || !this.websocket) throw new Error("WebSocket is not connected");
        this.websocket.send(JSON.stringify(message));
    }

    onMessage<T extends ReceiveMsg>(messageType: string, handler: MessageHandler<T>): () => void {
        if (!this.messageHandlers.has(messageType)) this.messageHandlers.set(messageType, []);
        this.messageHandlers.get(messageType)!.push(handler as MessageHandler);
        return () => {
            const handlers = this.messageHandlers.get(messageType);
            if (handlers) {
                const index = handlers.indexOf(handler as MessageHandler);
                if (index !== -1) handlers.splice(index, 1);
            }
        };
    }

    private handleMessage(message: ReceiveMsg): void {
        const handlers = this.messageHandlers.get(message.type);
        if (handlers) handlers.forEach((handler) => handler(message));
    }
}
