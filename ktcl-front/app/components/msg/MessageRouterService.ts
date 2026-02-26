import {TaskService} from "../domain";

export interface MessageRouterDependencies {
    ws: WebSocket;
    taskService: TaskService;
    onAuthSuccess: () => void;
}

/**
 * WebSocketメッセージハンドラを作成する
 */
export function createMessageHandler(deps: MessageRouterDependencies): (event: MessageEvent) => void {
    const {taskService, onAuthSuccess} = deps;

    return (event: MessageEvent) => {
        const msg = JSON.parse(event.data);
        console.debug("Received message:", msg);

        // auth_successメッセージを処理
        if (msg.type === "auth_success") {
            console.debug("Received auth_success message, calling onAuthSuccess callback");
            onAuthSuccess();
            return;
        }

        taskService.handleMessage(msg);
    };
}
