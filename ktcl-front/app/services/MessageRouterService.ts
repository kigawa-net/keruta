import {ProviderService, QueueService, TaskService} from "./domain";

export interface MessageRouterDependencies {
    ws: WebSocket;
    taskService: TaskService;
    queueService: QueueService;
    providerService: ProviderService;
}

/**
 * WebSocketメッセージハンドラを作成する
 */
export function createMessageHandler(deps: MessageRouterDependencies): (event: MessageEvent) => void {
    const {taskService, queueService, providerService} = deps;

    return (event: MessageEvent) => {
        const msg = JSON.parse(event.data);
        console.debug("Received message:", msg);
        taskService.handleMessage(msg);
        queueService.handleMessage(msg);
        providerService.handleMessage(msg);
    };
}
