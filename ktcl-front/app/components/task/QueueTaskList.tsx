import { useState } from "react";
import { useTaskService } from "../DomainContext";
import { Task } from "./types";
import { TaskListHeader } from "./TaskListHeader";
import { TaskListTable } from "./TaskListTable";

interface QueueTaskListProps {
  tasks: Task[];
  queues: { id: number; name: string }[];
  currentQueueId: number;
}

export function QueueTaskList({ tasks, queues, currentQueueId }: QueueTaskListProps) {
  const taskService = useTaskService();
  const [showCompleted, setShowCompleted] = useState(false);

  const filteredTasks = showCompleted ? tasks : tasks.filter((task) => task.status === "pending");

  const handleCompleteTask = (taskId: number) => {
    taskService.updateTask({ taskId, status: "completed" });
  };

  const handleMoveTask = (taskId: number, targetQueueId: number) => {
    taskService.moveTask({ taskId, targetQueueId });
  };

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <TaskListHeader showCompleted={showCompleted} onToggleShowCompleted={setShowCompleted} />
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
