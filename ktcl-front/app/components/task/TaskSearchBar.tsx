interface TaskSearchBarProps {
    searchQuery: string;
    onSearchChange: (query: string) => void;
}

const TaskSearchBar = ({ searchQuery, onSearchChange }: TaskSearchBarProps) => {
    return (
        <div className="flex gap-2">
            <input
                type="text"
                placeholder="タスク名で検索..."
                value={searchQuery}
                onChange={(e) => onSearchChange(e.target.value)}
                className="flex-1 px-4 py-2 border rounded focus:outline-none focus:ring-2"
                style={{
                    borderColor: '#dee2e6',
                    focusRing: '#0a58ca'
                }}
            />
            <button
                className="px-4 py-2 border rounded transition-colors"
                style={{
                    borderColor: '#dee2e6',
                    color: '#0a58ca'
                }}
                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f8f9fa'}
                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
            >
                フィルター
            </button>
        </div>
    );
};

export default TaskSearchBar;