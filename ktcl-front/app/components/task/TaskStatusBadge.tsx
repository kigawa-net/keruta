interface TaskStatusBadgeProps {
    status: string;
}

export function TaskStatusBadge({status}: TaskStatusBadgeProps) {
    const statusColors: Record<string, string> = {
        pending: "bg-yellow-100 text-yellow-800",
        in_progress: "bg-blue-100 text-blue-800",
        completed: "bg-green-100 text-green-800"
    };
    const statusLabels: Record<string, string> = {
        pending: "保留中",
        in_progress: "進行中",
        completed: "完了"
    };
    const colorClass = statusColors[status] || "bg-gray-100 text-gray-800";
    const label = statusLabels[status] || status;

    return (
        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${colorClass}`}>
            {label}
        </span>
    );
}