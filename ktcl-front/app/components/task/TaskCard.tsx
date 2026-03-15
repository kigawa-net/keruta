import {useState} from "react";
import {Task} from "./types";
import {TaskStatusBadge} from "./TaskStatusBadge";
import {TaskMoveModal} from "./TaskMoveModal";

interface TaskCardProps {
    task: Task;
    queues: { id: number; name: string }[];
    currentQueueId: number;
    onComplete: (taskId: number) => void;
    onMove: (taskId: number, targetQueueId: number) => void;
}

/**
 * モバイル表示用タスクリストカード
 */
export function TaskCard({task, queues, currentQueueId, onComplete, onMove}: TaskCardProps) {
    const [showMoveModal, setShowMoveModal] = useState(false);
    const [showLog, setShowLog] = useState(false);

    return (
        <div className="bg-white rounded-lg shadow border border-gray-200 p-4">
            <div className="flex justify-between items-start mb-3">
                <div>
                    <span className="text-xs text-gray-500">ID: {task.id}</span>
                    <h3 className="text-base font-medium text-gray-900">{task.title}</h3>
                </div>
                <TaskStatusBadge status={task.status}/>
            </div>

            {task.log && (
                <div className="mt-2">
                    <button
                        onClick={() => setShowLog(!showLog)}
                        className="text-xs text-blue-600 hover:underline"
                    >
                        {showLog ? "ログを隠す" : "ログを表示"}
                    </button>
                    {showLog && (
                        <pre className="mt-2 text-xs text-gray-700 whitespace-pre-wrap break-all max-h-48 overflow-y-auto font-mono bg-gray-50 rounded p-2">
                            {task.log}
                        </pre>
                    )}
                </div>
            )}

            {task.status !== "completed" && (
                <div className="flex gap-2 mt-3">
                    <button
                        onClick={() => onComplete(task.id)}
                        className="flex-1 inline-flex items-center justify-center px-3 py-2 text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                    >
                        完了
                    </button>
                    <button
                        onClick={() => setShowMoveModal(true)}
                        className="flex-1 inline-flex items-center justify-center px-3 py-2 text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
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
        </div>
    );
}
