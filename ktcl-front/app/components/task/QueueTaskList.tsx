import {ServerTaskMoveMsg, ServerTaskUpdateMsg} from "../../msg/task";
import {useWsState} from "../websocket/Websocket";
import {useState} from "react";
import {Task} from "./types";
import {TaskListHeader} from "./TaskListHeader";
import {TaskListTable} from "./TaskListTable";

interface QueueTaskListProps {
    tasks: Task[];
    queues: { id: number; name: string }[];
    currentQueueId: number;
}

export function QueueTaskList({tasks, queues, currentQueueId}: QueueTaskListProps) {
    const wsState = useWsState();
    const [showCompleted, setShowCompleted] = useState(false);

    const filteredTasks = showCompleted
        ? tasks
        : tasks.filter(task => task.status == "pending");

    const handleCompleteTask = (taskId: number) => {
        if (wsState.state !== "open") return;

        const msg: ServerTaskUpdateMsg = {
            type: "task_update",
            taskId: taskId,
            status: "completed"
        };
        wsState.websocket.send(JSON.stringify(msg));
    };

    const handleMoveTask = (taskId: number, targetQueueId: number) => {
        if (wsState.state !== "open") return;

        const msg: ServerTaskMoveMsg = {
            type: "task_move",
            taskId: taskId,
            targetQueueId: targetQueueId
        };
        wsState.websocket.send(JSON.stringify(msg));
    };

    return (
        <div className="bg-white rounded-lg shadow overflow-hidden">
            <TaskListHeader
                showCompleted={showCompleted}
                onToggleShowCompleted={setShowCompleted}
            />
            <TaskListTable
                tasks={filteredTasks}
                queues={queues}
                currentQueueId={currentQueueId}
                showCompleted={showCompleted}
                onCompleteTask={handleCompleteTask}
                onMoveTask={handleMoveTask}
            />
        </div>
    );
}
