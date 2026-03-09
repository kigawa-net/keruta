import {useKeycloakState} from "./Keycloak";
import {useUserProfileState} from "../user/UserProfile";


const AuthButton = () => {
    const kcState = useKeycloakState()
    const userProfile = useUserProfileState()
    if (kcState.state == "unloaded") return <div className="text-xs">loading</div>
    if (kcState.state == "unauthenticated") return (
        <button onClick={kcState.login} className="login-btn text-xs md:text-sm">
            Login
        </button>
    )

    const username = userProfile.state == "loaded" ? userProfile.value.username : "User"
    return (
        <div className="flex items-center gap-2 md:gap-4">
            <span className="hidden sm:inline text-xs md:text-sm">Welcome, {username}!</span>
            <span className="sm:hidden text-xs">{username}</span>
            <button onClick={kcState.logout} className="logout-btn text-xs md:text-sm">
                Logout
            </button>
        </div>
    )
}

export default AuthButton
