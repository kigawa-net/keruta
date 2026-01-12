import ServerConfig from "../ServerConfig.server";
import {exportJWK, importPKCS8} from "jose";

export async function loader() {
    const key = await ServerConfig.getPrivateKey()
    const jwk = await exportJWK(key)

    return {
        keys: [
            jwk
        ],
    };
}
