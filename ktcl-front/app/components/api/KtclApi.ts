import {Url} from "../../util/net/Url";
import {ApiClient} from "./ApiClient";

export class KtclApi {
    private readonly client: ApiClient

    constructor(baseUrl: Url) {
        this.client = new ApiClient(baseUrl)
    }

    /** サーバートークンの取得 */
    async getServerToken(userToken: string) {
        return this.client.post<{ token: string }>("/api/token", {token: userToken});
    }
}
