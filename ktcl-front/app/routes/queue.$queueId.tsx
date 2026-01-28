import {Route} from "../../.react-router/types/app/+types/root";
import LoaderArgs = Route.LoaderArgs;
import ComponentProps = Route.ComponentProps;
import PrivateRoute from "../components/PrivateRoute";
import {useEffect, useState} from "react";
import {useWsState} from "../components/websocket/Websocket";
import {useKerutaTaskState} from "../components/KerutaTask";
import useWsReceive from "../components/websocket/useWsReceive";
import {ClientTaskListedMsg, ServerTaskListMsg} from "../msg/task";

export async function loader({params}: LoaderArgs) {
    return {
        queueId: params.queueId
    }
}

type Task = ClientTaskListedMsg["queues"][0]

// noinspection JSUnusedGlobalSymbols
export default function Route({loaderData}: ComponentProps) {
    const wsState = useWsState()
    const kerutaState = useKerutaTaskState()
    const [tasks, setTasks] = useState<Task[]>([])

    useWsReceive(wsState, msg => {
        if (msg.type !== "task_listed") return
        setTasks(msg.queues)
    }, [])

    useEffect(() => {
        if (wsState.state !== "open") return
        if (kerutaState.state !== "connected") return
        if (kerutaState.auth.state !== "authenticated") return

        const msg: ServerTaskListMsg = {
            type: "task_list"
        }
        wsState.websocket.send(JSON.stringify(msg))
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

                    <div className="bg-white rounded-lg shadow overflow-hidden">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        ID
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        タスク名
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {tasks.length === 0 ? (
                                    <tr>
                                        <td colSpan={2} className="px-6 py-4 text-center text-gray-500">
                                            タスクがありません
                                        </td>
                                    </tr>
                                ) : (
                                    tasks.map(task => (
                                        <tr key={task.id} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                {task.id}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {task.name}
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </PrivateRoute>
    )
}
