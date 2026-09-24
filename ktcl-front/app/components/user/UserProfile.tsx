import {createContext, type ReactNode, useContext, useEffect, useState} from "react";
import {useKiseAuthState} from "../auth/KiseAuth";


const Context = createContext<UserProfileState>({state: "unloaded"});

export function UserProfileProvider(
    {
        children, ...props
    }: {
        children: ReactNode
    }) {
    const [userProfile, setUserProfile] = useState<UserProfileState>({state: "unloaded"})
    const authState = useKiseAuthState()
    useEffect(() => {
        if (authState.state != "authenticated") return
        setUserProfile({
            state: "loaded",
            value: {
                username: authState.preferredUsername ?? authState.sub,
                sub: authState.sub,
            },
        })
    }, [authState.state]);
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
    value: { username: string; sub: string }
}
