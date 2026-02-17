import PrivateRoute from "../components/PrivateRoute";
import {useEffect, useState} from "react";
import {useWsState, WsState} from "../components/websocket/Websocket";
import {KerutaTaskState, useKerutaTaskState} from "../components/KerutaTask";
import useWsReceive from "../components/websocket/useWsReceive";
import {ClientTaskListedMsg, ServerTaskListMsg} from "../msg/task";
import {ClientQueueListedMsg, ServerQueueListMsg} from "../msg/queue";
import {QueueTaskCreateForm} from "../components/task/QueueTaskCreateForm";
import {QueueTaskList} from "../components/task/QueueTaskList";
import {Route} from "../../.react-router/types/app/routes/+types/queue.$queueId";


type Task = ClientTaskListedMsg["tasks"][0]
type Queue = ClientQueueListedMsg["queues"][0]

// noinspection JSUnusedGlobalSymbols
export default function Page({params: {queueId}}: Route.ComponentProps) {
    const wsState = useWsState()
    const kerutaState = useKerutaTaskState()
    const [tasks, setTasks] = useState<Task[]>([])
    const [queues, setQueues] = useState<Queue[]>([])


    useWsReceive(wsState, msg => {
        if (msg.type === "task_listed") {
            setTasks(msg.tasks)
        } else if (msg.type === "queue_listed") {
            setQueues(msg.queues)
        } else if (msg.type === "task_created") {
            loadTaskList(wsState, kerutaState, Number(queueId))
        } else if (msg.type === "task_updated") {
            loadTaskList(wsState, kerutaState, Number(queueId))
        } else if (msg.type === "task_moved") {
            loadTaskList(wsState, kerutaState, Number(queueId))
        }
    }, [wsState.state, kerutaState.state === "connected" && kerutaState.auth.state, queueId])

    useEffect(() => {
        loadTaskList(wsState, kerutaState, Number(queueId))
        loadQueueList(wsState, kerutaState)
    }, [wsState.state, kerutaState.state === "connected" && kerutaState.auth.state, queueId])

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
                        queueId={queueId} onTaskCreated={() => loadTaskList(wsState, kerutaState, queueId)}
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

function loadTaskList(wsState: WsState, kerutaState: KerutaTaskState, queueId: number) {
    if (wsState.state !== "open") return
    if (kerutaState.state !== "connected") return
    if (kerutaState.auth.state !== "authenticated") return

    const msg: ServerTaskListMsg = {
        type: "task_list",
        queueId: queueId
    }
    wsState.websocket.send(JSON.stringify(msg))
}

function loadQueueList(wsState: WsState, kerutaState: KerutaTaskState) {
    if (wsState.state !== "open") return
    if (kerutaState.state !== "connected") return
    if (kerutaState.auth.state !== "authenticated") return

    const msg: ServerQueueListMsg = {
        type: "queue_list"
    }
    wsState.websocket.send(JSON.stringify(msg))
}
