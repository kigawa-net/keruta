import {ClientTaskListedMsg, ServerTaskUpdateMsg} from "../../msg/task";
import {useWsState} from "../websocket/Websocket";

type Task = ClientTaskListedMsg["tasks"][0];

interface QueueTaskListProps {
    tasks: Task[];
}

export function QueueTaskList({tasks}: QueueTaskListProps) {
    const wsState = useWsState();

    const handleCompleteTask = (taskId: number) => {
        if (wsState.state !== "open") return;

        const msg: ServerTaskUpdateMsg = {
            type: "task_update",
            taskId: taskId,
            status: "completed"
        };
        wsState.websocket.send(JSON.stringify(msg));
    };

    const getStatusBadge = (status: string) => {
        const statusColors: Record<string, string> = {
            pending: "bg-yellow-100 text-yellow-800",
            in_progress: "bg-blue-100 text-blue-800",
            completed: "bg-green-100 text-green-800"
        };
        const statusLabels: Record<string, string> = {
            pending: "保留中",
            in_progress: "進行中",
            completed: "完了"
        };
        const colorClass = statusColors[status] || "bg-gray-100 text-gray-800";
        const label = statusLabels[status] || status;

        return (
            <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${colorClass}`}>
                {label}
            </span>
        );
    };

    return (
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
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        ステータス
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        操作
                    </th>
                </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                {tasks === undefined || tasks.length === 0 ? (
                    <tr>
                        <td colSpan={4} className="px-6 py-4 text-center text-gray-500">
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
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {getStatusBadge(task.status)}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {task.status !== "completed" && (
                                    <button
                                        onClick={() => handleCompleteTask(task.id)}
                                        className="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                                    >
                                        完了
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
        </div>
    );
}