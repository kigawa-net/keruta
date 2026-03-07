import {useEffect, useState} from "react";
import {Link} from "react-router-dom";

import {useWebsocketReceive} from "../util/net/websocket/useWebsocketReceive";
import {ClientTaskListedMsg, ServerTaskListMsg} from "../components/msg/task";
import {ClientQueueListedMsg, ServerQueueListMsg} from "../components/msg/queue";
import {QueueTaskCreateForm} from "../components/task/QueueTaskCreateForm";
import {QueueTaskList} from "../components/task/QueueTaskList";
import {Route} from "../../.react-router/types/app/routes/+types/queue.$queueId";
import {useWebsocketState, WebsocketState} from "../util/net/websocket/WebsocketProvider";
import {AuthedKtseState, useAuthedKtseState} from "../components/api/AuthedKtseProvider";


type Task = ClientTaskListedMsg["tasks"][0]
type Queue = ClientQueueListedMsg["queues"][0]

// noinspection JSUnusedGlobalSymbols
export default function Page({params: {queueId}}: Route.ComponentProps) {
    const globalState = useWebsocketState()
    const authedKtse = useAuthedKtseState()
    const [tasks, setTasks] = useState<Task[]>([])
    const [queues, setQueues] = useState<Queue[]>([])


    useWebsocketReceive(msg => {
        if (msg.type === "task_listed") {
            setTasks(msg.tasks)
        } else if (msg.type === "queue_listed") {
            setQueues(msg.queues)
        } else if (msg.type === "task_created") {
            loadTaskList(globalState, authedKtse, Number(queueId))
        } else if (msg.type === "task_updated") {
            loadTaskList(globalState, authedKtse, Number(queueId))
        } else if (msg.type === "task_moved") {
            loadTaskList(globalState, authedKtse, Number(queueId))
        }
    }, [globalState.state, authedKtse.state, queueId])

    useEffect(() => {
        loadTaskList(globalState, authedKtse, Number(queueId))
        loadQueueList(globalState, authedKtse)
    }, [globalState.state, authedKtse.state, queueId])

    const currentQueue = queues.find(q => q.id === Number(queueId))
    const queueDisplayName = currentQueue?.name || `Queue ${queueId}`

    return (
        <div className="p-3 md:p-8">
            <div className="max-w-6xl mx-auto">
                <div className="mb-6 md:mb-8 flex items-start justify-between gap-4">
                    <div>
                        <h1 className="text-2xl md:text-3xl font-bold text-gray-900">
                            タスク一覧 - {queueDisplayName}
                        </h1>
                        <p className="mt-2 text-gray-600 hidden sm:block">このキューに含まれるタスクの一覧を表示します</p>
                    </div>
                    <Link
                        to={`/queue/${queueId}/edit`}
                        className="shrink-0 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        編集
                    </Link>
                </div>

                <QueueTaskCreateForm
                    queueId={queueId}
                    onTaskCreated={() => loadTaskList(globalState, authedKtse, Number(queueId))}
                />

                <div className="mt-4 md:mt-6">
                    <QueueTaskList
                        tasks={tasks}
                        queues={queues}
                        currentQueueId={Number(queueId)}
                    />
                </div>
            </div>
        </div>
    )
}

function loadTaskList(globalState: WebsocketState, authedKtse: AuthedKtseState, queueId: number) {
    if (globalState.state !== "open") return
    if (authedKtse.state !== "loaded") return
    if (!Number.isFinite(queueId)) return

    const msg: ServerTaskListMsg = {
        type: "task_list",
        queueId: queueId
    }
    globalState.websocket.send(JSON.stringify(msg))
}

function loadQueueList(globalState: WebsocketState, authedKtse: AuthedKtseState) {
    if (globalState.state !== "open") return
    if (authedKtse.state !== "loaded") return

    const msg: ServerQueueListMsg = {
        type: "queue_list"
    }
    globalState.websocket.send(JSON.stringify(msg))
}
