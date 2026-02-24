import {ReceiveMsg, SendMsgType} from "../msg/msg";
import {MutableStateFlow} from "../../util/StateFlow";
import {KtseAuthApi} from "../auth/KtseAuthApi";

export class KtseApi {
    private readonly receiver: MutableStateFlow<ReceiveMsg>
    readonly auth: KtseAuthApi

    private listener(event: MessageEvent) {
        if (this.receiver == null) {
            console.error(
                "received message but receiver is null:",
                event.data
            )
            return
        }
        this.receiver.call(JSON.parse(event.data))
    }

    constructor(private readonly websocket: WebSocket) {
        this.receiver = new MutableStateFlow<ReceiveMsg>()
        websocket.addEventListener("message",(evt)=>{
            console.debug("received message:", this.receiver)
            this.listener(evt)
        })
        this.auth = new KtseAuthApi(this)
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
