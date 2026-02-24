import {ClientProviderListMsg, ServerProviderListMsg} from "../msg/provider";
import {KtseApi} from "./KtseApi";
import {MutableStateFlow} from "../../util/StateFlow";

export class AuthedKtseApi {
    private readonly providerListed = new MutableStateFlow<ClientProviderListMsg>()

    constructor(readonly ktse: KtseApi) {
    }

    private readonly providerListedId = this.ktse.getReceiver().addListener((msg) => {
        if (msg.type === "provider_listed") {
            this.receiveProviderListed.call(msg)
        }
    })

    close() {
        this.ktse.getReceiver().removeListener(this.providerListedId)
    }

    receiveProviderListed() {
        return this.providerListed
    }

    sendProviderList() {
        const res: ServerProviderListMsg = {
            type: "provider_list",
        }
        this.ktse.send(res)
    }
}
