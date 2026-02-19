import PrivateRoute from '../components/auth/PrivateRoute'
import TaskCreatePage from "../components/task/TaskCreatePage";

// noinspection JSUnusedGlobalSymbols
export default function TaskCreateRoute() {
    return (
        <PrivateRoute>
            <TaskCreatePage/>
        </PrivateRoute>
    )
}
