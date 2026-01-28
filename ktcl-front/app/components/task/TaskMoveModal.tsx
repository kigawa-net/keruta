import React, {useState} from "react";

interface TaskMoveModalProps {
    task: {
        id: number;
        title: string;
    };
    queues: {
        id: number;
        name: string;
    }[];
    currentQueueId: number;
    onMove: (targetQueueId: number) => void;
    onClose: () => void;
}

export function TaskMoveModal({task, queues, currentQueueId, onMove, onClose}: TaskMoveModalProps) {
    const [selectedQueueId, setSelectedQueueId] = useState<number | null>(null);

    const availableQueues = queues.filter(q => q.id !== currentQueueId);

    const handleMove = () => {
        if (selectedQueueId !== null) {
            onMove(selectedQueueId);
            onClose();
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
                <h2 className="text-xl font-bold mb-4">タスクを移動</h2>
                <p className="text-gray-600 mb-4">
                    タスク「{task.title}」を移動する先のキューを選択してください
                </p>

                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        移動先キュー
                    </label>
                    <select
                        className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        value={selectedQueueId ?? ""}
                        onChange={(e) => setSelectedQueueId(Number(e.target.value))}
                    >
                        <option value="">キューを選択してください</option>
                        {availableQueues.map((queue) => (
                            <option key={queue.id} value={queue.id}>
                                {queue.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                    >
                        キャンセル
                    </button>
                    <button
                        onClick={handleMove}
                        disabled={selectedQueueId === null}
                        className="px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-300 disabled:cursor-not-allowed"
                    >
                        移動
                    </button>
                </div>
            </div>
        </div>
    );
}