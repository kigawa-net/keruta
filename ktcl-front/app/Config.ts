import {Url} from "./util/net/Url";

const strWebsocketUrl = import.meta.env.VITE_WEBSOCKET_URL ?? process.env.VITE_WEBSOCKET_URL;
const Config = {
    websocketUrl: strWebsocketUrl ? Url.parse(strWebsocketUrl) : undefined as unknown as Url,
    kiseUrl: import.meta.env.VITE_KISE_URL ?? process.env.VITE_KISE_URL,
} as const

function validate() {
    for (const key in Config) {
        if (Config[key] === undefined) throw new Error(`undefined env: ${key}`)
    }
}

validate()
export default Config
