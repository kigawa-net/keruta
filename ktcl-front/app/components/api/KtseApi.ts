import {ReceiveMsg, SendMsgType} from "../msg/msg";
import {MutableStateFlow} from "../../util/StateFlow";
import {KtseAuthApi} from "../auth/KtseAuthApi";

export class KtseApi {
    private readonly receiver = new MutableStateFlow<ReceiveMsg>()
    readonly auth = new KtseAuthApi(this)

    private listener(event: MessageEvent) {
        if (this.receiver == null) {
            console.error(
                "Received message:",
                event.data
            )
            return
        }
        this.receiver.call(JSON.parse(event.data))
    }

    constructor(private readonly websocket: WebSocket) {
        websocket.addEventListener("message", this.listener)
    }

    close() {
        this.auth.close()
        this.websocket.removeEventListener("message", this.listener)
    }

    getReceiver() {
        return this.receiver
    }

    send<T extends { type: SendMsgType }>(message: T): void {
        this.websocket.send(JSON.stringify(message));
    }
}
