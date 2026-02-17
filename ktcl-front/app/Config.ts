const Config = {
    websocketUrl: new URL(import.meta.env.VITE_WEBSOCKET_URL),
    userIssuer: import.meta.env.VITE_USER_ISSUER,
    userClientId: import.meta.env.VITE_USER_CLIENT_ID,
    ownIssuer: import.meta.env.VITE_OWN_ISSUER,
} as const
export default Config
