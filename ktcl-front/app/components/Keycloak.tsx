import {
    createContext,
    type Dispatch,
    type ReactNode,
    type SetStateAction,
    useContext,
    useEffect,
    useState
} from "react";
import Keycloak from "keycloak-js";
import {keycloak} from "../keycloak";


const Context = createContext<KeycloakState>({
    state: "unloaded",
});

export function KeycloakProvider(
    {
        children,
        ...props
    }: {
        children: ReactNode | undefined
    }) {
    const [keycloakState, setKeycloakState] = useState<KeycloakState>({state: "unloaded"})

    useEffect(() => initKeycloak(setKeycloakState), []);

    return <Context.Provider
        value={keycloakState}
        {...props}
    >
        {children}
    </Context.Provider>;
}


function initKeycloak(
    setKeycloak: Dispatch<SetStateAction<KeycloakState>>,
) {
    keycloak.onAuthSuccess = () => {
        setKeycloak(updateKeycloakState({state: "authenticated"}))
    }
    keycloak.onAuthRefreshSuccess = () => {
        setKeycloak(updateKeycloakState({state: "authenticated"}))
    }
    keycloak.onAuthLogout = () => {
        setKeycloak(updateKeycloakState({state: "unauthenticated"}))
    }
    keycloak.onAuthRefreshError = () => {
        setKeycloak(updateKeycloakState({state: "unauthenticated"}))
    }
    keycloak.onAuthError = () => {
        setKeycloak(updateKeycloakState({state: "unauthenticated"}))
    }
    keycloak.onReady = () => {
        setKeycloak(updateKeycloakState({
            state: keycloak.authenticated ? "authenticated" : "unauthenticated"
        }))
    }
    if (!keycloak.didInitialize) keycloak.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        checkLoginIframe: false
    }).then(value => {
        if (!value) {
            console.log("failed to initialize keycloak")
        }
    })
}

function updateKeycloakState(
    state: { state: "unauthenticated" } | { state: "authenticated" }
): KeycloakState {
    if (state.state == "authenticated") return {
        state: state.state,
        keycloak,
        logout() {
            return keycloak.logout()
        },
        async getToken(): Promise<string> {
            await keycloak.updateToken(60)
            return keycloak.token
        }
    }
    return {
        state: state.state,
        keycloak,
        login() {
            return keycloak.login()
        }
    }
}

export function useKeycloakState() {
    return useContext(Context);
}

export type KeycloakState = {
    state: "unloaded"
} | {
    state: "unauthenticated",
    keycloak: Keycloak,
    login(): void
} | AuthenticatedKcState

export interface AuthenticatedKcState {
    state: "authenticated",
    keycloak: Keycloak,

    logout(): void

    getToken(): Promise<string>
}
