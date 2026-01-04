import { useAuth } from '../hooks/useAuth'

const AuthButton = () => {
    const { authenticated, login, logout, userInfo } = useAuth()

    if (authenticated) {
        return (
            <div className="auth-section">
                <span>Welcome, {userInfo?.preferred_username || userInfo?.name || 'User'}!</span>
                <button onClick={logout} className="logout-btn">
                    Logout
                </button>
            </div>
        )
    }

    return (
        <div className="auth-section">
            <button onClick={login} className="login-btn">
                Login with Keycloak
            </button>
        </div>
    )
}

export default AuthButton