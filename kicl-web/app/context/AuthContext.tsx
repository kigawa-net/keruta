import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import type { User, UserManager } from 'oidc-client-ts';
import { createUserManager } from '../lib/auth';
import { SETTINGS_STORAGE_KEY, defaultSettings, type AppSettings } from '../types/settings';

interface AuthContextType {
    user: User | null;
    isLoading: boolean;
    login: () => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
    user: null,
    isLoading: true,
    login: () => {},
    logout: () => {},
});

function loadSettings(): AppSettings {
    const stored = localStorage.getItem(SETTINGS_STORAGE_KEY);
    return stored ? { ...defaultSettings, ...(JSON.parse(stored) as Partial<AppSettings>) } : defaultSettings;
}

function buildManager(): UserManager | null {
    const settings = loadSettings();
    if (!settings.userIssuerUrl || !settings.oidcClientId) return null;
    return createUserManager(settings.userIssuerUrl, settings.oidcClientId);
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [manager, setManager] = useState<UserManager | null>(null);

    useEffect(() => {
        const um = buildManager();
        setManager(um);

        if (!um) {
            setIsLoading(false);
            return;
        }

        um.getUser().then((u) => {
            setUser(u);
            setIsLoading(false);
        });

        const onLoaded = (u: User) => setUser(u);
        const onUnloaded = () => setUser(null);
        um.events.addUserLoaded(onLoaded);
        um.events.addUserUnloaded(onUnloaded);

        return () => {
            um.events.removeUserLoaded(onLoaded);
            um.events.removeUserUnloaded(onUnloaded);
        };
    }, []);

    function login() {
        if (!manager) {
            window.location.href = '/settings';
            return;
        }
        manager.signinRedirect();
    }

    function logout() {
        if (!manager) return;
        manager.signoutRedirect({ post_logout_redirect_uri: window.location.origin });
    }

    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
