import {Route} from "../../.react-router/types/app/+types/root";
import PrivateRoute from "../components/PrivateRoute";
import {useEffect, useState} from "react";
import {useWsState} from "../components/websocket/Websocket";
import {useKerutaTaskState} from "../components/KerutaTask";
import useWsReceive from "../components/websocket/useWsReceive";
import {ClientTaskListedMsg, ServerTaskCreateMsg, ServerTaskListMsg} from "../msg/task";
import FormTextInput, {InputValue} from "../components/form/FormTextInput";
import FormErrMsg from "../components/form/FormErrMsg";
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
    const [formState, setFormState] = useState<"inputting" | "submitting">("inputting")
    const [description, setDescription] = useState<InputValue>({value: ""})
    const [taskName, setTaskName] = useState<InputValue>({value: ""})
    const [err, setErr] = useState<string>()

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
            setTaskName({value: ""})
            setDescription({value: ""})
            setFormState("inputting")
            loadTaskList()
        }
    }, [])

    useEffect(() => {
        loadTaskList()
    }, [wsState.state, kerutaState.state === "connected" && kerutaState.auth.state])

    useEffect(() => {
        if (formState !== "submitting") return
        if (wsState.state !== "open") {
            setErr("websocket is not connected")
            setFormState("inputting")
            return
        }
        if (taskName.value.trim() === "") {
            setTaskName({error: "タスク名を入力してください", ...taskName})
            setFormState("inputting")
            return
        }

        const msg: ServerTaskCreateMsg = {
            type: "task_create",
            queueId: parseInt(loaderData.queueId),
            title: taskName.value,
            description: description.value.trim(),
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [formState])

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

                    {/* タスク作成フォーム */}
                    <div className="bg-white rounded-lg shadow p-6 mb-6">
                        <h2 className="text-xl font-semibold text-gray-900 mb-4">新しいタスクを作成</h2>
                        <form className="space-y-4" onSubmit={(event) => {
                            event.preventDefault()
                            setFormState("submitting")
                        }}>
                            <FormTextInput
                                label="タスク名"
                                id="taskName"
                                placeholder="タスク名を入力してください"
                                value={taskName}
                                onChange={setTaskName}
                            />
                            <FormTextInput
                                label="タスクの説明"
                                id="description"
                                placeholder="タスクの説明を入力してください"
                                value={description}
                                onChange={setDescription}
                            />
                            <div className="flex gap-4">
                                <button
                                    type="submit"
                                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400"
                                    disabled={formState === "submitting"}
                                >
                                    {formState === "submitting" ? "作成中..." : "タスクを作成"}
                                </button>
                            </div>
                            <FormErrMsg err={err}/>
                        </form>
                    </div>

                    {/* タスク一覧 */}
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
                            {tasks == undefined || tasks.length === 0 ? (
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
                                            {task.title}
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
