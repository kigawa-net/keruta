import {createContext, type ReactNode, useContext, useEffect, useState} from "react";
import Config from "../../Config";

const KISE_TOKEN_KEY = "kise_token"

export type KiseAuthState = {
    state: "unloaded"
} | {
    state: "unauthenticated"
    login(): void
} | {
    state: "authenticated"
    logout(): void
    getToken(): Promise<string>
    sub: string
    preferredUsername: string | null
}

const Context = createContext<KiseAuthState>({state: "unloaded"})

export function KiseAuthProvider({children}: { children: ReactNode }) {
    const [state, setState] = useState<KiseAuthState>({state: "unloaded"})

    useEffect(() => {
        const token = sessionStorage.getItem(KISE_TOKEN_KEY)
        if (token && !isTokenExpired(token)) {
            const payload = decodeTokenPayload(token)
            setState(makeAuthenticatedState(token, payload?.sub ?? "", payload?.preferred_username ?? null, setState))
        } else {
            if (token) sessionStorage.removeItem(KISE_TOKEN_KEY)
            setState(makeUnauthenticatedState())
        }
    }, [])

    return <Context.Provider value={state}>{children}</Context.Provider>
}

function makeUnauthenticatedState(): KiseAuthState {
    return {
        state: "unauthenticated",
        login() {
            window.location.href = `${Config.kiseUrl}/login`
        },
    }
}

function makeAuthenticatedState(
    token: string,
    sub: string,
    preferredUsername: string | null,
    setState: (state: KiseAuthState) => void,
): KiseAuthState {
    return {
        state: "authenticated",
        sub,
        preferredUsername,
        logout() {
            sessionStorage.removeItem(KISE_TOKEN_KEY)
            setState(makeUnauthenticatedState())
        },
        async getToken() {
            if (isTokenExpired(token)) {
                sessionStorage.removeItem(KISE_TOKEN_KEY)
                setState(makeUnauthenticatedState())
                throw new Error("Token expired")
            }
            return token
        },
    }
}

function decodeTokenPayload(token: string): Record<string, unknown> | null {
    try {
        return JSON.parse(atob(token.split(".")[1]))
    } catch {
        return null
    }
}

function isTokenExpired(token: string): boolean {
    try {
        const payload = decodeTokenPayload(token)
        const exp = payload?.exp
        if (typeof exp !== "number") return true
        return exp * 1000 < Date.now()
    } catch {
        return true
    }
}

export function storeKiseToken(token: string) {
    sessionStorage.setItem(KISE_TOKEN_KEY, token)
}

export function useKiseAuthState(): KiseAuthState {
    return useContext(Context) ?? {state: "unloaded"}
}
