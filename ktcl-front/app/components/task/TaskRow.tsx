import {useState} from "react";
import {Task} from "./types";
import {TaskStatusBadge} from "./TaskStatusBadge";
import {TaskMoveModal} from "./TaskMoveModal";
import {TaskLogModal} from "./TaskLogModal";

const STATUS_OPTIONS = [
    {value: "", label: "未設定"},
    {value: "completed", label: "完了"},
    {value: "failed", label: "失敗"},
]

interface TaskRowProps {
    task: Task;
    queues: { id: number; name: string }[];
    currentQueueId: number;
    onStatusChange: (taskId: number, status: string) => void;
    onMove: (taskId: number, targetQueueId: number) => void;
}

export function TaskRow({task, queues, currentQueueId, onStatusChange, onMove}: TaskRowProps) {
    const [showMoveModal, setShowMoveModal] = useState(false);
    const [showLogModal, setShowLogModal] = useState(false);
    return (
        <>
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
                    <div className="flex gap-2 items-center">
                        {task.log && (
                            <button
                                onClick={() => setShowLogModal(true)}
                                className="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-gray-600 hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
                            >
                                ログ
                            </button>
                        )}
                        <select
                            value={task.status}
                            onChange={(e) => onStatusChange(task.id, e.target.value)}
                            className="px-2 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                        >
                            {STATUS_OPTIONS.map(opt => (
                                <option key={opt.value} value={opt.value}>{opt.label}</option>
                            ))}
                            {!STATUS_OPTIONS.find(o => o.value === task.status) && (
                                <option value={task.status}>{task.status}</option>
                            )}
                        </select>
                        <button
                            onClick={() => setShowMoveModal(true)}
                            className="inline-flex items-center px-3 py-1 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                        >
                            移動
                        </button>
                    </div>
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
            {showLogModal && task.log && (
                <TaskLogModal
                    title={task.title}
                    log={task.log}
                    onClose={() => setShowLogModal(false)}
                />
            )}
        </>
    );
}
