import {
    ClientProviderAddTokenMsg,
    ClientProviderDeletedMsg,
    ClientProviderIdpAddedMsg,
    ClientProviderListMsg,
    ServerProviderIssueTokenMsg,
    ServerProviderListMsg
} from "../msg/provider";
import {KtseApi} from "./KtseApi";
import {MutableStateFlow} from "../../util/StateFlow";
import {
    ClientQueueCreatedMsg,
    ClientQueueShowedMsg,
    ClientQueueUpdatedMsg,
    ServerQueueCreateMsg,
    ServerQueueShowMsg,
    ServerQueueUpdateMsg
} from "../msg/queue";
import {ServerTaskCreateMsg, ServerTaskMoveMsg, ServerTaskUpdateMsg} from "../msg/task";

export class AuthedKtseApi {
    readonly providerListed = new MutableStateFlow<ClientProviderListMsg>()
    readonly queueCreated = new MutableStateFlow<ClientQueueCreatedMsg>()
    readonly queueShowed = new MutableStateFlow<ClientQueueShowedMsg>()
    readonly queueUpdated = new MutableStateFlow<ClientQueueUpdatedMsg>()
    readonly providerIdpAdded = new MutableStateFlow<ClientProviderIdpAddedMsg>()
    readonly providerDeleted = new MutableStateFlow<ClientProviderDeletedMsg>()
    readonly providerTokenIssued = new MutableStateFlow<ClientProviderAddTokenMsg>()

    constructor(readonly ktse: KtseApi) {
        this.ktse.getReceiver().addListener((msg) => {
            switch (msg.type) {
                case "queue_created":
                    this.queueCreated.call(msg);
                    break;
                case "queue_showed":
                    this.queueShowed.call(msg);
                    break;
                case "queue_updated":
                    this.queueUpdated.call(msg);
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

    createQueue(providerId: number, name: string, setting: string): void {
        const msg: ServerQueueCreateMsg = {type: "queue_create", providerId, name, setting};
        this.ktse.send(msg);
    }

    showQueue(id: number): void {
        const msg: ServerQueueShowMsg = {type: "queue_show", id};
        this.ktse.send(msg);
    }

    updateQueue(queueId: number, name: string): void {
        const msg: ServerQueueUpdateMsg = {type: "queue_update", queueId, name};
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

    updateTask(taskId: number, status: string): void {
        const msg: ServerTaskUpdateMsg = {type: "task_update", taskId, status};
        this.ktse.send(msg);
    }

    moveTask(taskId: number, targetQueueId: number): void {
        const msg: ServerTaskMoveMsg = {type: "task_move", taskId, targetQueueId};
        this.ktse.send(msg);
    }

    providerIssuerToken(issuer: string): void {
        const msg: ServerProviderIssueTokenMsg = {type: "provider_issue_token", issuer};
        this.ktse.send(msg);
    }
}
