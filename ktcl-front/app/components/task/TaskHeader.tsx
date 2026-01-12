interface TaskHeaderProps {
    onCreateTask: () => void;
}

const TaskHeader = ({ onCreateTask }: TaskHeaderProps) => {
    return (
        <div className="flex items-center justify-between mb-4">
            <h1 className="text-3xl font-bold" style={{color: '#0a58ca'}}>
                タスク管理
            </h1>
            <button
                onClick={onCreateTask}
                className="px-4 py-2 rounded font-medium transition-colors"
                style={{
                    backgroundColor: '#0a58ca',
                    color: 'white'
                }}
                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#084298'}
                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#0a58ca'}
            >
                + 新規タスク作成
            </button>
        </div>
    );
};

export default TaskHeader;