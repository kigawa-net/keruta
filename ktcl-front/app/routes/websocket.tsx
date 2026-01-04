import PrivateRoute from '../components/PrivateRoute'
import WebSocketDemo from "../../src/pages/WebSocketDemo";

// noinspection JSUnusedGlobalSymbols
export default function WebSocketRoute() {
    return (
        <PrivateRoute>
            <WebSocketDemo/>
        </PrivateRoute>
    )
}
