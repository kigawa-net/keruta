import {Task} from "./types";
import {TaskStatusBadge} from "./TaskStatusBadge";

interface TaskRowProps {
    task: Task;
    onComplete: (taskId: number) => void;
}

export function TaskRow({task, onComplete}: TaskRowProps) {
    return (
        <tr className="hover:bg-gray-50">
            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                {task.id}
            </td>
            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {task.title}
            </td>
            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                <TaskStatusBadge status={task.status}/>
            </td>
            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {task.status !== "completed" && (
                    <button
                        onClick={() => onComplete(task.id)}
                        className="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                    >
                        完了
                    </button>
                )}
            </td>
        </tr>
    );
}