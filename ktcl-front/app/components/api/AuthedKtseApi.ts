import {ClientProviderListMsg, ServerProviderListMsg} from "../msg/provider";
import {KtseApi} from "./KtseApi";
import {MutableStateFlow} from "../../util/StateFlow";
import {ClientQueueCreatedMsg, ServerQueueCreateMsg, ServerQueueListMsg} from "../msg/queue";

export class AuthedKtseApi {
    readonly providerListed = new MutableStateFlow<ClientProviderListMsg>()
    readonly queueCreated = new MutableStateFlow<ClientQueueCreatedMsg>()

    constructor(readonly ktse: KtseApi) {
    }

    private readonly providerListedId = this.ktse.getReceiver().addListener((msg) => {
        switch (msg.type) {
            case "queue_created":
                this.queueCreated.call(msg);
                break;
            case "provider_listed":
                this.providerListed.call(msg);
                break;
        }
    })

    close() {
        this.ktse.getReceiver().removeListener(this.providerListedId)
    }
    sendProviderList() {
        const res: ServerProviderListMsg = {
            type: "provider_list",
        }
        this.ktse.send(res)
    }

    createQueue(providerId: number, name: string): void {
        const msg: ServerQueueCreateMsg = {type: "queue_create", providerId, name};
        this.ktse.send(msg);
    }

    listQueues(): void {
        const msg: ServerQueueListMsg = {type: "queue_list"};
        this.ktse.send(msg);
    }
}
