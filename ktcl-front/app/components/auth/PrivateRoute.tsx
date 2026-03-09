import type {ReactNode} from "react";
import {useKeycloakState} from "./Keycloak";

interface PrivateRouteProps {
    children: ReactNode
}

const PrivateRoute = ({children}: PrivateRouteProps) => {
    const kcState = useKeycloakState()

    if (kcState.state == "unloaded") return <div>loading</div>
    if (kcState.state == "unauthenticated") {
        return (
            <div className="private-route">
                <h2>Authentication Required</h2>
                <p>You need to log in to access this page.</p>
                <button onClick={() => {
                    kcState.login()
                }} className="login-btn">
                    Login with Keycloak
                </button>
            </div>
        )
    }

    return <>{children}</>
}

export default PrivateRoute
