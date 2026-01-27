import PrivateRoute from '../components/PrivateRoute'
import TaskCreatePage from "../components/TaskCreatePage";

// noinspection JSUnusedGlobalSymbols
export default function TaskCreateRoute() {
    return (
        <PrivateRoute>
            <TaskCreatePage/>
        </PrivateRoute>
    )
}
