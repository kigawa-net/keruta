const ServerConfig = {
    privateKey: process.env.PRIVATE_KEY,
    ownIssuer: process.env.OWN_ISSUER,
} as const
export default ServerConfig
