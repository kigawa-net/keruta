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
import {keycloakClient} from "./keycloak.client";


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
    keycloakClient.onAuthSuccess = () => {
        setKeycloak(updateKeycloakState({state: "authenticated"}))
    }
    keycloakClient.onAuthRefreshSuccess = () => {
        setKeycloak(updateKeycloakState({state: "authenticated"}))
    }
    keycloakClient.onAuthLogout = () => {
        setKeycloak(updateKeycloakState({state: "unauthenticated"}))
    }
    keycloakClient.onAuthRefreshError = () => {
        setKeycloak(updateKeycloakState({state: "unauthenticated"}))
    }
    keycloakClient.onAuthError = () => {
        setKeycloak(updateKeycloakState({state: "unauthenticated"}))
    }
    keycloakClient.onReady = () => {
        setKeycloak(updateKeycloakState({
            state: keycloakClient.authenticated ? "authenticated" : "unauthenticated"
        }))
    }
    if (!keycloakClient.didInitialize) keycloakClient.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        checkLoginIframe: false,
        pkceMethod: 'S256',
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
        keycloak: keycloakClient,
        logout() {
            return keycloakClient.logout()
        },
        async getToken(): Promise<string> {
            await keycloakClient.updateToken(60)
            return keycloakClient.token
        }
    }
    return {
        state: state.state,
        keycloak: keycloakClient,
        login() {
            return keycloakClient.login()
        }
    }
}

export function useKeycloakState(): KeycloakState {
    return useContext(Context) || {state: "unloaded"};
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
