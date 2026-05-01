import {Url} from "../../util/net/Url";
import {ApiClient} from "./ApiClient";
import {KerutaWellKnownJson} from "./WellKnownTypes";

export class KtclApi {
    private readonly client: ApiClient

    constructor(baseUrl: Url) {
        this.client = new ApiClient(baseUrl)
    }

    /** サーバートークンの取得 */
    async getServerToken(userToken: string) {
        return this.client.post<{ token: string }>("/api/token", {token: userToken});
    }

    /** プロバイダーの issuer から keruta.json を取得 */
    async getWellKnown(issuerUrl: string): Promise<KerutaWellKnownJson> {
        const url = issuerUrl.replace(/\/$/, "") + "/.well-known/keruta.json";
        return fetch(url).then(r => r.json());
    }
}
