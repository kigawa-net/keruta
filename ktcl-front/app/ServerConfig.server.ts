const ServerConfig = {
    strPrivateKey: process.env.PRIVATE_KEY,
    ownIssuer: process.env.OWN_ISSUER,
    ktseAud: process.env.KTSE_AUD,
    userJwksUrl: process.env.USER_JWKS_URL,
    userIssuer: process.env.USER_ISSUER,
} as const
export default ServerConfig
