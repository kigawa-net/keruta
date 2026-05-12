const ServerConfig = {
    strPrivateKey: process.env.PRIVATE_KEY,
    ownIssuer: process.env.OWN_ISSUER,
    ktseAud: process.env.KTSE_AUD,
    // kise サーバーの URL（/.well-known/openid-configuration を提供する）
    userIssuer: process.env.USER_ISSUER,
} as const

function validate() {
    for (const key in ServerConfig) {
        if (ServerConfig[key] === undefined) throw new Error(`ServerConfig undefined env: ${key}`)
    }
}

validate()
export default ServerConfig
