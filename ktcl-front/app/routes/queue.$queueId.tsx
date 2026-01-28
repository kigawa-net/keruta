import {Route} from "../../.react-router/types/app/+types/root";
import PrivateRoute from "../components/PrivateRoute";
import {useEffect, useState} from "react";
import {useWsState} from "../components/websocket/Websocket";
import {useKerutaTaskState} from "../components/KerutaTask";
import useWsReceive from "../components/websocket/useWsReceive";
import {ClientTaskListedMsg, ServerTaskListMsg} from "../msg/task";
import {QueueTaskCreateForm} from "../components/task/QueueTaskCreateForm";
import {QueueTaskList} from "../components/task/QueueTaskList";
import LoaderArgs = Route.LoaderArgs;
import ComponentProps = Route.ComponentProps;

export async function loader({params}: LoaderArgs) {
    return {
        queueId: params.queueId
    }
}

type Task = ClientTaskListedMsg["tasks"][0]

// noinspection JSUnusedGlobalSymbols
export default function Route({loaderData}: ComponentProps) {
    const wsState = useWsState()
    const kerutaState = useKerutaTaskState()
    const [tasks, setTasks] = useState<Task[]>([])

    const loadTaskList = () => {
        if (wsState.state !== "open") return
        if (kerutaState.state !== "connected") return
        if (kerutaState.auth.state !== "authenticated") return

        const msg: ServerTaskListMsg = {
            type: "task_list",
            queueId: loaderData.queueId
        }
        wsState.websocket.send(JSON.stringify(msg))
    }

    useWsReceive(wsState, msg => {
        if (msg.type === "task_listed") {
            setTasks(msg.tasks)
        } else if (msg.type === "task_created") {
            loadTaskList()
        } else if (msg.type === "task_updated") {
            // タスクが更新されたら一覧を再読み込み
            loadTaskList()
        }
    }, [])

    useEffect(() => {
        loadTaskList()
    }, [wsState.state, kerutaState.state === "connected" && kerutaState.auth.state])

    return (
        <PrivateRoute>
            <div className="h-full p-8">
                <div className="max-w-6xl mx-auto">
                    <div className="mb-8">
                        <h1 className="text-3xl font-bold text-gray-900">
                            タスク一覧 - Queue {loaderData.queueId}
                        </h1>
                        <p className="mt-2 text-gray-600">このキューに含まれるタスクの一覧を表示します</p>
                    </div>

                    <QueueTaskCreateForm queueId={loaderData.queueId} onTaskCreated={loadTaskList} />

                    <QueueTaskList tasks={tasks} />
                </div>
            </div>
        </PrivateRoute>
    )
}
