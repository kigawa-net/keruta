import {KtseApi} from "../api/KtseApi";
import {ClientAuthSuccessMsg, ServerAuthRequestMsg} from "../msg/auth";
import {MutableStateFlow} from "../../util/StateFlow";

export class KtseAuthApi {
    private readonly authSuccessReceiver
        = new MutableStateFlow<ClientAuthSuccessMsg>()
    private readonly authSuccessId = this.ktseApi.getReceiver().addListener((msg) => {
        if (msg.type === "auth_success") {
            this.authSuccessReceiver.call(msg)
        }
    })

    constructor(private readonly ktseApi: KtseApi) {
    }

    close() {
        this.authSuccessReceiver.removeListener(this.authSuccessId)
    }

    getAuthSuccessReceiver() {
        return this.authSuccessReceiver
    }

    sendAuthRequest(userToken: string, serverToken: string): void {
        const msg: ServerAuthRequestMsg = {
            type: "auth_request",
            userToken,
            serverToken,
        };
        this.ktseApi.send(msg);
    }

}
