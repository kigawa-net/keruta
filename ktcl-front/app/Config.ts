import {Url} from "./util/net/Url";

const Config = {
    websocketUrl: Url.parse(import.meta.env.VITE_WEBSOCKET_URL),
    kiseUrl: import.meta.env.VITE_KISE_URL,
} as const

function validate() {
    for (const key in Config) {
        if (Config[key] === undefined) throw new Error(`undefined env: ${key}`)
    }
}

validate()
export default Config
