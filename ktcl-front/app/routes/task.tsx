import PrivateRoute from '../components/auth/PrivateRoute'
import TaskPage from "../components/task/TaskPage";

// noinspection JSUnusedGlobalSymbols
export default function TaskRoute() {
    return (
        <PrivateRoute>
            <TaskPage/>
        </PrivateRoute>
    )
}
