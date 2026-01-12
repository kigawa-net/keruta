import {EncryptJWT, importPKCS8} from "jose";

const ServerConfig = {
    strPrivateKey: process.env.PRIVATE_KEY,
    ownIssuer: process.env.OWN_ISSUER,
    ktseAud: process.env.KTSE_AUD,
    async getPrivateKey() {
        const encodedKey = this.strPrivateKey
        return await importPKCS8(
            Buffer.from(encodedKey, "base64").toString(),
            "RS256",
            {
                extractable: true
            }
        )
    },
    async getJwt() {
        const privateKey = await this.getPrivateKey()
        return await new EncryptJWT()
            .setIssuer(this.ownIssuer)
            .setAudience(this.ktseAud)
            .setExpirationTime("1m")
            .encrypt(privateKey)
    }
} as const
export default ServerConfig
