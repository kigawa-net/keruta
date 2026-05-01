interface TaskListHeaderProps {
    showCompleted: boolean;
    onToggleShowCompleted: (show: boolean) => void;
}

export function TaskListHeader({showCompleted, onToggleShowCompleted}: TaskListHeaderProps) {
    return (
        <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
            <h2 className="text-lg font-semibold text-gray-900">タスク一覧</h2>
            <label className="flex items-center cursor-pointer">
                <input
                    type="checkbox"
                    checked={showCompleted}
                    onChange={(e) => onToggleShowCompleted(e.target.checked)}
                    className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <span className="ml-2 text-sm text-gray-700">完了済みを表示</span>
            </label>
        </div>
    );
}