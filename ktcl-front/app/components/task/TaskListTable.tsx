import {Task} from "./types";
import {TaskRow} from "./TaskRow";

interface TaskListTableProps {
    tasks: Task[];
    showCompleted: boolean;
    onCompleteTask: (taskId: number) => void;
}

export function TaskListTable({tasks, showCompleted, onCompleteTask}: TaskListTableProps) {
    return (
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
                        {showCompleted ? "タスクがありません" : "未完了のタスクがありません"}
                    </td>
                </tr>
            ) : (
                tasks.map(task => (
                    <TaskRow key={task.id} task={task} onComplete={onCompleteTask}/>
                ))
            )}
            </tbody>
        </table>
    );
}