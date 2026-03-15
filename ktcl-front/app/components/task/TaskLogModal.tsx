interface TaskLogModalProps {
    title: string;
    log: string;
    onClose: () => void;
}

export function TaskLogModal({title, log, onClose}: TaskLogModalProps) {
    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
            onClick={onClose}
        >
            <div
                className="bg-white rounded-lg shadow-xl w-full max-w-3xl max-h-[80vh] flex flex-col mx-4"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="flex items-center justify-between px-6 py-4 border-b">
                    <h2 className="text-lg font-semibold text-gray-900 truncate mr-4">
                        実行ログ — {title}
                    </h2>
                    <button
                        onClick={onClose}
                        className="shrink-0 text-gray-500 hover:text-gray-700"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                  d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                    </button>
                </div>
                <div className="flex-1 overflow-y-auto p-4">
                    <pre className="text-xs text-gray-800 whitespace-pre-wrap break-all font-mono bg-gray-50 rounded p-4">
                        {log}
                    </pre>
                </div>
            </div>
        </div>
    );
}
