import type {ReactNode} from "react";
import {useKiseAuthState} from "./KiseAuth";

interface PrivateRouteProps {
    children: ReactNode
}

const PrivateRoute = ({children}: PrivateRouteProps) => {
    const authState = useKiseAuthState()

    if (authState.state == "unloaded") return <div>loading</div>
    if (authState.state == "unauthenticated") {
        return (
            <div className="private-route">
                <h2>Authentication Required</h2>
                <p>You need to log in to access this page.</p>
                <button onClick={() => {
                    authState.login()
                }} className="login-btn">
                    Login
                </button>
            </div>
        )
    }

    return <>{children}</>
}

export default PrivateRoute
