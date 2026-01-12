import PrivateRoute from '../components/PrivateRoute'
import TaskCreatePage from "../pages/TaskCreatePage";

// noinspection JSUnusedGlobalSymbols
export default function TaskCreateRoute() {
    return (
        <PrivateRoute>
            <TaskCreatePage/>
        </PrivateRoute>
    )
}