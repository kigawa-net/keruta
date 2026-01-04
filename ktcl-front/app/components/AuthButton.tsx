import {useKeycloakState} from "./Keycloak.tsx";
import {useUserProfileState} from "./UserProfile.tsx";

const AuthButton = () => {
    const kcState = useKeycloakState()
    const userProfile = useUserProfileState()
    if (kcState.state == "unloaded") return <div>loading</div>
    if (kcState.state == "unauthenticated") return (
        <div className="auth-section">
            <button onClick={kcState.login} className="login-btn">
                Login with Keycloak
            </button>
        </div>
    )

    const username = userProfile.state == "loaded" ? userProfile.value.username : "User"
    return (
        <div className="auth-section">
            <span>Welcome, {username}!</span>
            <button onClick={kcState.logout} className="logout-btn">
                Logout
            </button>
        </div>
    )
}

export default AuthButton
