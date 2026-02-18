import {ProviderService, QueueService, TaskService} from "../domain";

export interface MessageRouterDependencies {
    ws: WebSocket;
    taskService: TaskService;
    queueService: QueueService;
    providerService: ProviderService;
    onAuthSuccess: () => void;
}

/**
 * WebSocketメッセージハンドラを作成する
 */
export function createMessageHandler(deps: MessageRouterDependencies): (event: MessageEvent) => void {
    const {taskService, queueService, providerService, onAuthSuccess} = deps;

    return (event: MessageEvent) => {
        const msg = JSON.parse(event.data);
        console.debug("Received message:", msg);

        // auth_successメッセージを処理
        if (msg.type === "auth_success") {
            onAuthSuccess();
            return;
        }

        taskService.handleMessage(msg);
        queueService.handleMessage(msg);
        providerService.handleMessage(msg);
    };
}
