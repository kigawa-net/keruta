export const Config = {
    websocketUrl: new URL(import.meta.env.VITE_WEBSOCKET_URL),
} as const
