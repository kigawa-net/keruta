import ServerConfig from "../ServerConfig.server";
import {exportJWK, importPKCS8} from "jose";

export async function loader() {
    const encodedKey = ServerConfig.privateKey
    const key = await importPKCS8(
        Buffer.from(encodedKey, "base64").toString(),
        "RS256",
        {
            extractable: true
        }
    )
    const jwk = await exportJWK(key)

    return {
        keys: [
            jwk
        ],
    };
}
