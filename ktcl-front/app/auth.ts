import {createRemoteJWKSet, exportJWK, importPKCS8, jwtVerify, SignJWT} from "jose";
import ServerConfig from "./ServerConfig.server";

export namespace Auth {
    let cachedOidcConfig: {jwks_uri: string; issuer: string} | null = null
    let cachedJwks: ReturnType<typeof createRemoteJWKSet> | null = null

    async function getOidcConfig() {
        if (cachedOidcConfig) return cachedOidcConfig
        const issuerUrl = ServerConfig.userIssuer.replace(/\/$/, "")
        const response = await fetch(`${issuerUrl}/.well-known/openid-configuration`)
        if (!response.ok) throw new Error(`Failed to fetch OIDC configuration: ${response.statusText}`)
        cachedOidcConfig = await response.json()
        return cachedOidcConfig
    }

    export async function getUserJwks() {
        if (cachedJwks) return cachedJwks
        const config = await getOidcConfig()
        cachedJwks = createRemoteJWKSet(new URL(config.jwks_uri))
        return cachedJwks
    }

    export async function verifyUserJwt(jwt: string) {
        const jwks = await getUserJwks()
        const config = await getOidcConfig()
        return jwtVerify(jwt, jwks, {issuer: config.issuer, audience: ServerConfig.ktseAud})
    }

    export async function getPrivateKey() {
        const encodedKey = ServerConfig.strPrivateKey
        if (!encodedKey || encodedKey.trim() === "") {
            throw new Error("PRIVATE_KEY environment variable is not set or empty")
        }
        return await importPKCS8(
            Buffer.from(encodedKey, "base64").toString(),
            "RS256",
            {
                extractable: true,
            }
        )
    }

    export async function getJwks() {
        const key = await Auth.getPrivateKey()
        return await exportJWK(key)
    }

    export async function getJwt(sub: string) {
        const privateKey = await getPrivateKey()
        return await new SignJWT()
            .setProtectedHeader({alg: "RS256"})
            .setIssuer(ServerConfig.ownIssuer)
            .setAudience(ServerConfig.ktseAud)
            .setSubject(sub)
            .setExpirationTime("1m")
            .sign(privateKey)
    }
}
