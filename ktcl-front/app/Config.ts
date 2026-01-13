 const Config = {
    websocketUrl: new URL(import.meta.env.VITE_WEBSOCKET_URL),
    keycloak: {
        url: import.meta.env.VITE_KEYCLOAK_URL,
        realm: import.meta.env.VITE_KEYCLOAK_REALM,
        clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
    },
} as const
export default Config
