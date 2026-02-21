import PrivateRoute from "../components/auth/PrivateRoute";
import {useEffect, useState} from "react";

import useWsReceive from "../components/net/websocket/useWsReceive";
import {ClientTaskListedMsg, ServerTaskListMsg} from "../components/msg/task";
import {ClientQueueListedMsg, ServerQueueListMsg} from "../components/msg/queue";
import {QueueTaskCreateForm} from "../components/task/QueueTaskCreateForm";
import {QueueTaskList} from "../components/task/QueueTaskList";
import {Route} from "../../.react-router/types/app/routes/+types/queue.$queueId";
import {useKerutaTaskState} from "../components/app/useAppState";
import {KerutaTaskState} from "../components/net/websocket/ConnectionStateTypes";
import {GlobalState, useGlobalState} from "../components/app/Global";


type Task = ClientTaskListedMsg["tasks"][0]
type Queue = ClientQueueListedMsg["queues"][0]

// noinspection JSUnusedGlobalSymbols
export default function Page({params: {queueId}}: Route.ComponentProps) {
    const globalState = useGlobalState()
    const kerutaState = useKerutaTaskState()
    const [tasks, setTasks] = useState<Task[]>([])
    const [queues, setQueues] = useState<Queue[]>([])


    useWsReceive(globalState, msg => {
        if (msg.type === "task_listed") {
            setTasks(msg.tasks)
        } else if (msg.type === "queue_listed") {
            setQueues(msg.queues)
        } else if (msg.type === "task_created") {
            loadTaskList(globalState, kerutaState, Number(queueId))
        } else if (msg.type === "task_updated") {
            loadTaskList(globalState, kerutaState, Number(queueId))
        } else if (msg.type === "task_moved") {
            loadTaskList(globalState, kerutaState, Number(queueId))
        }
    }, [globalState.state, kerutaState.state === "connected" && kerutaState.auth.state, queueId])

    useEffect(() => {
        loadTaskList(globalState, kerutaState, Number(queueId))
        loadQueueList(globalState, kerutaState)
    }, [globalState.state, kerutaState.state === "connected" && kerutaState.auth.state, queueId])

    const currentQueue = queues.find(q => q.id === Number(queueId))
    const queueDisplayName = currentQueue?.name || `Queue ${queueId}`

    return (
        <PrivateRoute>
            <div className="h-full p-8">
                <div className="max-w-6xl mx-auto">
                    <div className="mb-8">
                        <h1 className="text-3xl font-bold text-gray-900">
                            タスク一覧 - {queueDisplayName}
                        </h1>
                        <p className="mt-2 text-gray-600">このキューに含まれるタスクの一覧を表示します</p>
                    </div>

                    <QueueTaskCreateForm
                        queueId={queueId} onTaskCreated={() => loadTaskList(globalState, kerutaState, queueId)}
                    />

                    <QueueTaskList
                        tasks={tasks}
                        queues={queues}
                        currentQueueId={queueId}
                    />
                </div>
            </div>
        </PrivateRoute>
    )
}

function loadTaskList(globalState: GlobalState, kerutaState: KerutaTaskState, queueId: number) {
    if (globalState.state !== "open") return
    if (kerutaState.state !== "connected") return
    if (kerutaState.auth.state !== "authenticated") return

    const msg: ServerTaskListMsg = {
        type: "task_list",
        queueId: queueId
    }
    globalState.websocket.send(JSON.stringify(msg))
}

function loadQueueList(globalState: GlobalState, kerutaState: KerutaTaskState) {
    if (globalState.state !== "open") return
    if (kerutaState.state !== "connected") return
    if (kerutaState.auth.state !== "authenticated") return

    const msg: ServerQueueListMsg = {
        type: "queue_list"
    }
    globalState.websocket.send(JSON.stringify(msg))
}
