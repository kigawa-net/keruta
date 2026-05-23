import { redirect } from 'react-router';
import { UserManager, WebStorageStateStore, type UserManagerSettings } from 'oidc-client-ts';
import { SETTINGS_STORAGE_KEY, defaultSettings, type AppSettings } from '../types/settings';

export function createUserManager(issuerUrl: string, clientId: string): UserManager {
    const settings: UserManagerSettings = {
        authority: issuerUrl,
        client_id: clientId,
        redirect_uri: `${window.location.origin}/auth/callback`,
        scope: 'openid profile email',
        userStore: new WebStorageStateStore({ store: window.localStorage }),
        automaticSilentRenew: false,
    };
    return new UserManager(settings);
}

export function loadSettings(): AppSettings {
    const stored = localStorage.getItem(SETTINGS_STORAGE_KEY);
    return stored ? { ...defaultSettings, ...(JSON.parse(stored) as Partial<AppSettings>) } : defaultSettings;
}

/** 認証が必要なルートのclientLoaderで使用する。未設定→/settings、未認証→/loginにリダイレクト */
export async function requireAuth(): Promise<{ userManager: UserManager }> {
    const settings = loadSettings();
    if (!settings.userIssuerUrl || !settings.oidcClientId) {
        throw redirect('/settings');
    }
    const userManager = createUserManager(settings.userIssuerUrl, settings.oidcClientId);
    const user = await userManager.getUser();
    if (!user || user.expired) {
        throw redirect('/login');
    }
    return { userManager };
}
