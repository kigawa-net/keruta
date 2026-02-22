import {Url} from "../../util/net/Url";

export class ApiClient {
    constructor(
        private readonly baseUrl: Url | null
    ) {
    }

    async fetch(endpoint: string, init: RequestInit) {
        const joinedEndpoint = this.baseUrl === null ? endpoint : this.baseUrl.toString() + endpoint
        return fetch(joinedEndpoint, init)
    }

    async post<T>(endpoint: string, body: any): Promise<T> {
        return this
            .fetch(endpoint, {method: "POST", body: JSON.stringify(body)})
            .then(value => value.json());
    }
}
