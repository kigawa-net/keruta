import {createContext, type ReactNode, useContext, useEffect, useState} from "react";
import {useKeycloakState} from "./Keycloak";
import type {KeycloakProfile} from "keycloak-js";


const Context = createContext<UserProfileState>({state: "unloaded"});

export function UserProfileProvider(
    {
        children, ...props
    }: {
        children: ReactNode
    }) {
    const [userProfile, setUserProfile] = useState<UserProfileState>({state: "unloaded"})
    const kcState = useKeycloakState()
    useEffect(() => {
        if (kcState.state != "authenticated") return
        kcState.keycloak.loadUserProfile().then(value => {
            setUserProfile({state: "loaded", value})
        })
    }, [kcState.state]);
    return <Context.Provider
        value={userProfile}
        {...props}
    >
        {children}
    </Context.Provider>;
}

export function useUserProfileState() {
    return useContext(Context);
}

export type UserProfileState = {
    state: "unloaded"
} | {
    state: "loaded",
    value: KeycloakProfile
}
