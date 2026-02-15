import {Task} from '../../types/task';

interface TaskDetailPanelProps {
    task: Task | null;
    onClose: () => void;
}

const TaskDetailPanel = ({ task, onClose }: TaskDetailPanelProps) => {
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

    if (!task) {
        return (
            <div className="w-full md:w-96 bg-white rounded-lg shadow-sm border p-6" style={{borderColor: '#dee2e6'}}>
                <div className="flex items-center justify-center h-full">
                    <div className="text-center" style={{color: '#6c757d'}}>
                        <svg className="w-16 h-16 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor"
                             viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
                                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                        </svg>
                        <p className="text-sm">
                            タスクを選択してください
                        </p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="w-full md:w-96 bg-white rounded-lg shadow-sm border p-6" style={{borderColor: '#dee2e6'}}>
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold" style={{color: '#0a58ca'}}>
                    タスク詳細
                </h2>
                <button
                    onClick={onClose}
                    className="text-gray-500 hover:text-gray-700"
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                              d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                </button>
            </div>

            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-semibold mb-1" style={{color: '#6c757d'}}>
                        タスクID
                    </label>
                    <div className="text-sm" style={{color: '#212529'}}>
                        {task.id}
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-semibold mb-1" style={{color: '#6c757d'}}>
                        タスク名
                    </label>
                    <div className="text-sm font-medium" style={{color: '#212529'}}>
                        {task.name}
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-semibold mb-1" style={{color: '#6c757d'}}>
                        ステータス
                    </label>
                    <span
                        className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(task.status)}`}>
                        {getStatusText(task.status)}
                    </span>
                </div>

                <div>
                    <label className="block text-sm font-semibold mb-1" style={{color: '#6c757d'}}>
                        作成日時
                    </label>
                    <div className="text-sm" style={{color: '#212529'}}>
                        {task.createdAt}
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-semibold mb-1" style={{color: '#6c757d'}}>
                        更新日時
                    </label>
                    <div className="text-sm" style={{color: '#212529'}}>
                        {task.updatedAt}
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-semibold mb-2" style={{color: '#6c757d'}}>
                        実行ログ
                    </label>
                    <div className="bg-gray-50 rounded p-3 text-xs font-mono" style={{
                        maxHeight: '200px',
                        overflowY: 'auto',
                        color: '#212529'
                    }}>
                        [2026-01-12 10:00:00] タスク開始<br/>
                        [2026-01-12 10:02:30] データ読み込み完了<br/>
                        [2026-01-12 10:04:15] 処理中...<br/>
                        {task.status === 'completed' && '[2026-01-12 10:05:00] タスク完了'}
                        {task.status === 'failed' && '[2026-01-12 10:05:00] エラー: 処理に失敗しました'}
                    </div>
                </div>

                <div className="pt-4 flex gap-2">
                    <button
                        className="flex-1 px-4 py-2 border rounded font-medium transition-colors"
                        style={{
                            borderColor: '#0a58ca',
                            color: '#0a58ca'
                        }}
                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f8f9fa'}
                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                    >
                        再実行
                    </button>
                    <button
                        className="flex-1 px-4 py-2 rounded font-medium transition-colors"
                        style={{
                            backgroundColor: '#dc3545',
                            color: 'white'
                        }}
                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#bb2d3b'}
                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#dc3545'}
                    >
                        削除
                    </button>
                </div>
            </div>
        </div>
    );
};

export default TaskDetailPanel;