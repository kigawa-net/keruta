import {Url} from "../Url";

export namespace Ws {
    export function connect(wsUrl: Url) {
        console.log("connect", wsUrl.toStrUrl())
        return new WebSocket(wsUrl.toJsURL())
    }
}
