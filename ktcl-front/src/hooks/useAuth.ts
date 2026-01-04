import {useKeycloak} from '@react-keycloak/web'
import * as Keycloak from 'keycloak-js'

export interface AuthContextType {
    keycloak: Keycloak.KeycloakInstance
    authenticated: boolean
    login: () => void
    logout: () => void
    userInfo: any
}

export const useAuth = (): AuthContextType => {
    const {keycloak} = useKeycloak()

    const login = () => {
        keycloak.login()
    }

    const logout = () => {
        keycloak.logout()
    }

    return {
        keycloak,
        authenticated: keycloak.authenticated ?? false,
        login,
        logout,
        userInfo: keycloak.tokenParsed,
    }
}
