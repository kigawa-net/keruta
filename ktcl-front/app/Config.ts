const Config = {
    websocketUrl: new URL(import.meta.env.VITE_WEBSOCKET_URL),
    userIssuer: import.meta.env.VITE_USER_ISSUER,
    userClientId: import.meta.env.VITE_USER_CLIENT_ID,
} as const

function validate() {
    for (const key in Config) {
        if (Config[key] === undefined) throw new Error(`undefined env: ${key}`)
    }
}

validate()
export default Config
