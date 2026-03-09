export interface Task {
    id: string;
    name: string;
    status: 'pending' | 'running' | 'completed' | 'failed';
    createdAt: string;
    updatedAt: string;
}