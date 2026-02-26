import {
    ClientProviderAddTokenMsg,
    ClientProviderDeletedMsg,
    ClientProviderIdpAddedMsg,
    ClientProviderListMsg,
    ServerProviderAddMsg,
    ServerProviderListMsg
} from "../msg/provider";
import {KtseApi} from "./KtseApi";
import {MutableStateFlow} from "../../util/StateFlow";
import {ClientQueueCreatedMsg, ServerQueueCreateMsg, ServerQueueListMsg} from "../msg/queue";
import {ServerTaskCreateMsg} from "../msg/task";

export class AuthedKtseApi {
    readonly providerListed = new MutableStateFlow<ClientProviderListMsg>()
    readonly queueCreated = new MutableStateFlow<ClientQueueCreatedMsg>()
    readonly providerIdpAdded = new MutableStateFlow<ClientProviderIdpAddedMsg>()
    readonly providerDeleted = new MutableStateFlow<ClientProviderDeletedMsg>()
    readonly providerTokenIssued = new MutableStateFlow<ClientProviderAddTokenMsg>()

    constructor(readonly ktse: KtseApi) {
        this.ktse.getReceiver().addListener((msg) => {
            switch (msg.type) {
                case "queue_created":
                    this.queueCreated.call(msg);
                    break;
                case "provider_listed":
                    this.providerListed.call(msg);
                    break;
                case "provider_add_token_issued":
                    this.providerTokenIssued.call(msg);
                    break;
                case "provider_idp_added":
                    this.providerIdpAdded.call(msg);
                    break;
                case "provider_deleted":
                    this.providerDeleted.call(msg);
                    break;
            }
        })
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

    createTask(queueId: number, title: string, description: string): void {
        const msg: ServerTaskCreateMsg = {
            type: "task_create",
            queueId: queueId,
            title: title,
            description: description,
        };
        this.ktse.send(msg);
    }

    addProvider(name: string, issuer: string): void {
        const msg: ServerProviderAddMsg = {type: "provider_add", name, issuer};
        this.ktse.send(msg);
    }
}
