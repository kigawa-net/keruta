import {useCallback, useState} from "react";
import {Task} from "./types";
import {TaskListHeader} from "./TaskListHeader";
import {TaskListTable} from "./TaskListTable";
import {useAuthedKtseState} from "../api/AuthedKtseProvider";

interface QueueTaskListProps {
    tasks: Task[];
    queues: { id: number; name: string }[];
    currentQueueId: number;
}

export function QueueTaskList({tasks, queues, currentQueueId}: QueueTaskListProps) {
    const [showCompleted, setShowCompleted] = useState(false);

    const filteredTasks = showCompleted ? tasks : tasks.filter((task) => task.status !== "completed");
    const authedKtse = useAuthedKtseState()
    const handleStatusChange = useCallback((taskId: number, status: string) => {
        if (authedKtse.state !== "loaded") return
        authedKtse.authedKtseApi.updateTask(taskId, status)
    }, [authedKtse]);
    const handleMoveTask = useCallback((taskId: number, targetQueueId: number) => {
        if (authedKtse.state !== "loaded") return
        authedKtse.authedKtseApi.moveTask(taskId, targetQueueId)
    }, [authedKtse]);

    return (
        <div className="bg-white rounded-lg shadow overflow-hidden">
            <TaskListHeader showCompleted={showCompleted} onToggleShowCompleted={setShowCompleted}/>
            <TaskListTable
                tasks={filteredTasks}
                queues={queues}
                currentQueueId={currentQueueId}
                showCompleted={showCompleted}
                onStatusChange={handleStatusChange}
                onMoveTask={handleMoveTask}
            />
        </div>
    );
}
