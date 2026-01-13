import {createRemoteJWKSet, exportJWK, importPKCS8, jwtVerify, SignJWT} from "jose";
import ServerConfig from "./ServerConfig.server";

export namespace Auth {
    export const userJwks = createRemoteJWKSet(new URL(ServerConfig.userJwksUrl))

    export async function verifyUserJwt(jwt: string) {
        return jwtVerify(jwt, userJwks, {issuer: ServerConfig.userIssuer, audience: ServerConfig.ktseAud})
    }

    export async function getPrivateKey() {
        const encodedKey = ServerConfig.strPrivateKey
        return await importPKCS8(
            Buffer.from(encodedKey, "base64").toString(),
            "RS256",
            {
                extractable: true
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
