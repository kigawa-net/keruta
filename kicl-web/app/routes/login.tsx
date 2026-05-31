import { redirect } from 'react-router';
import { createUserManager, loadSettings } from '../lib/auth';
import { useAuth } from '../context/AuthContext';

export function HydrateFallback() {
    return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">読み込み中...</div>;
}

export async function clientLoader(): Promise<null> {
    const settings = loadSettings();
    if (!settings.userIssuerUrl || !settings.oidcClientId) {
        throw redirect('/settings');
    }
    const userManager = createUserManager(settings.userIssuerUrl, settings.oidcClientId);
    const user = await userManager.getUser();
    if (user && !user.expired) {
        throw redirect('/');
    }
    return null;
}

export default function Login() {
    const { login } = useAuth();

    return (
        <div className="flex min-h-[60vh] items-center justify-center">
            <div className="w-full max-w-sm rounded-lg border border-gray-200 bg-white p-8 text-center shadow-sm">
                <h1 className="mb-2 text-2xl font-bold text-gray-900">kicl</h1>
                <p className="mb-6 text-sm text-gray-500">
                    このページにアクセスするにはログインが必要です。
                </p>
                <button
                    onClick={login}
                    className="w-full rounded bg-blue-600 px-5 py-2 text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    ログイン
                </button>
            </div>
        </div>
    );
}
