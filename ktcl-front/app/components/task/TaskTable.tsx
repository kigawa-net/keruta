import {Task} from '../../types/task';

interface TaskTableProps {
    tasks: Task[];
    onTaskSelect: (task: Task) => void;
}

const TaskTable = ({ tasks, onTaskSelect }: TaskTableProps) => {
    const getStatusColor = (status: Task['status']) => {
        const colors = {
            pending: 'bg-gray-100 text-gray-800',
            running: 'bg-blue-100 text-blue-800',
            completed: 'bg-green-100 text-green-800',
            failed: 'bg-red-100 text-red-800'
        };
        return colors[status];
    };

    const getStatusText = (status: Task['status']) => {
        const texts = {
            pending: '待機中',
            running: '実行中',
            completed: '完了',
            failed: '失敗'
        };
        return texts[status];
    };

    return (
        <div className="flex-1 bg-white rounded-lg shadow-sm border overflow-hidden" style={{borderColor: '#dee2e6'}}>
            <div className="overflow-x-auto">
                <table className="w-full">
                    <thead className="border-b" style={{
                        backgroundColor: '#f8f9fa',
                        borderColor: '#dee2e6'
                    }}>
                    <tr>
                        <th className="px-6 py-3 text-left text-sm font-semibold" style={{color: '#212529'}}>
                            タスク名
                        </th>
                        <th className="px-6 py-3 text-left text-sm font-semibold" style={{color: '#212529'}}>
                            ステータス
                        </th>
                        <th className="px-6 py-3 text-left text-sm font-semibold" style={{color: '#212529'}}>
                            作成日時
                        </th>
                        <th className="px-6 py-3 text-left text-sm font-semibold" style={{color: '#212529'}}>
                            更新日時
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {tasks.map((task) => (
                        <tr
                            key={task.id}
                            className="border-b cursor-pointer transition-colors"
                            style={{borderColor: '#dee2e6'}}
                            onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f8f9fa'}
                            onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                            onClick={() => onTaskSelect(task)}
                        >
                            <td className="px-6 py-4 text-sm" style={{color: '#212529'}}>
                                {task.name}
                            </td>
                            <td className="px-6 py-4">
                                <span
                                    className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(task.status)}`}>
                                    {getStatusText(task.status)}
                                </span>
                            </td>
                            <td className="px-6 py-4 text-sm" style={{color: '#6c757d'}}>
                                {task.createdAt}
                            </td>
                            <td className="px-6 py-4 text-sm" style={{color: '#6c757d'}}>
                                {task.updatedAt}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default TaskTable;