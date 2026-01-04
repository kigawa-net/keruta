import {useAuth} from '../hooks/useAuth'
import type {ReactNode} from "react";

interface PrivateRouteProps {
    children: ReactNode
}

const PrivateRoute = ({children}: PrivateRouteProps) => {
    const {authenticated, login} = useAuth()

    if (!authenticated) {
        return (
            <div className="private-route">
                <h2>Authentication Required</h2>
                <p>You need to log in to access this page.</p>
                <button onClick={login} className="login-btn">
                    Login with Keycloak
                </button>
            </div>
        )
    }

    return <>{children}</>
}

export default PrivateRoute
