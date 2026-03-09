import {useState} from "react";
import {Task} from "./types";
import {TaskStatusBadge} from "./TaskStatusBadge";
import {TaskMoveModal} from "./TaskMoveModal";

interface TaskRowProps {
    task: Task;
    queues: { id: number; name: string }[];
    currentQueueId: number;
    onComplete: (taskId: number) => void;
    onMove: (taskId: number, targetQueueId: number) => void;
}

export function TaskRow({task, queues, currentQueueId, onComplete, onMove}: TaskRowProps) {
    const [showMoveModal, setShowMoveModal] = useState(false);
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
                    <div className="flex gap-2">
                        <button
                            onClick={() => onComplete(task.id)}
                            className="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                        >
                            完了
                        </button>
                        <button
                            onClick={() => setShowMoveModal(true)}
                            className="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                        >
                            移動
                        </button>
                    </div>
                )}
                {showMoveModal && (
                    <TaskMoveModal
                        task={task}
                        queues={queues}
                        currentQueueId={currentQueueId}
                        onMove={(targetQueueId) => onMove(task.id, targetQueueId)}
                        onClose={() => setShowMoveModal(false)}
                    />
                )}
            </td>
        </tr>
    );
}