import {ClientTaskListedMsg} from "../../msg/task";

type Task = ClientTaskListedMsg["tasks"][0];

interface QueueTaskListProps {
    tasks: Task[];
}

export function QueueTaskList({tasks}: QueueTaskListProps) {
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
                </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                {tasks === undefined || tasks.length === 0 ? (
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
    );
}