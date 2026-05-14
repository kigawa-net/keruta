import { redirect } from 'react-router';
import { createUserManager } from '../lib/auth';
import { SETTINGS_STORAGE_KEY, defaultSettings, type AppSettings } from '../types/settings';

export function HydrateFallback() {
    return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">認証処理中...</div>;
}

export async function clientLoader(): Promise<never> {
    const stored = localStorage.getItem(SETTINGS_STORAGE_KEY);
    const settings: AppSettings = stored
        ? { ...defaultSettings, ...(JSON.parse(stored) as Partial<AppSettings>) }
        : defaultSettings;

    if (!settings.userIssuerUrl || !settings.oidcClientId) {
        throw redirect('/settings');
    }

    const userManager = createUserManager(settings.userIssuerUrl, settings.oidcClientId);
    await userManager.signinRedirectCallback();
    throw redirect('/account');
}

export default function AuthCallback() {
    return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">認証処理中...</div>;
}
