import {useState} from 'react';
import {Task} from '../types/task';
import TaskHeader from './task/TaskHeader';
import TaskSearchBar from './task/TaskSearchBar';
import TaskTable from './task/TaskTable';
import TaskPagination from './task/TaskPagination';
import TaskDetailPanel from './task/TaskDetailPanel';

const TaskPage = () => {
    const [selectedTask, setSelectedTask] = useState<Task | null>(null);
    const [searchQuery, setSearchQuery] = useState('');

    // ダミーデータ
    const tasks: Task[] = [
        {
            id: '1',
            name: 'データ処理タスク',
            status: 'running',
            createdAt: '2026-01-12 10:00:00',
            updatedAt: '2026-01-12 10:05:00'
        },
        {
            id: '2',
            name: 'レポート生成',
            status: 'completed',
            createdAt: '2026-01-12 09:30:00',
            updatedAt: '2026-01-12 09:45:00'
        },
        {
            id: '3',
            name: 'バックアップ処理',
            status: 'pending',
            createdAt: '2026-01-12 11:00:00',
            updatedAt: '2026-01-12 11:00:00'
        },
        {
            id: '4',
            name: '画像変換処理',
            status: 'failed',
            createdAt: '2026-01-12 08:00:00',
            updatedAt: '2026-01-12 08:10:00'
        }
    ];

    const filteredTasks = tasks.filter(task =>
        task.name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
        <div className="flex h-full gap-6">
            {/* タスク一覧 */}
            <div className="flex-1 flex flex-col">
                <div className="mb-6">
                    <TaskHeader />
                    <TaskSearchBar
                        searchQuery={searchQuery}
                        onSearchChange={setSearchQuery}
                    />
                </div>

                <TaskTable
                    tasks={filteredTasks}
                    onTaskSelect={setSelectedTask}
                />

                <TaskPagination totalCount={filteredTasks.length} />
            </div>

            {/* タスク詳細パネル */}
            <TaskDetailPanel
                task={selectedTask}
                onClose={() => setSelectedTask(null)}
            />
        </div>
    );
};

export default TaskPage;
