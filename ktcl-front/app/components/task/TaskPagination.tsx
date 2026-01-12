interface TaskPaginationProps {
    totalCount: number;
}

const TaskPagination = ({ totalCount }: TaskPaginationProps) => {
    return (
        <div className="mt-4 flex items-center justify-between">
            <div className="text-sm" style={{color: '#6c757d'}}>
                全 {totalCount} 件のタスク
            </div>
            <div className="flex gap-2">
                <button
                    className="px-3 py-1 border rounded transition-colors"
                    style={{
                        borderColor: '#dee2e6',
                        color: '#6c757d'
                    }}
                    disabled
                >
                    前へ
                </button>
                <button
                    className="px-3 py-1 border rounded"
                    style={{
                        backgroundColor: '#0a58ca',
                        borderColor: '#0a58ca',
                        color: 'white'
                    }}
                >
                    1
                </button>
                <button
                    className="px-3 py-1 border rounded transition-colors"
                    style={{
                        borderColor: '#dee2e6',
                        color: '#6c757d'
                    }}
                    disabled
                >
                    次へ
                </button>
            </div>
        </div>
    );
};

export default TaskPagination;