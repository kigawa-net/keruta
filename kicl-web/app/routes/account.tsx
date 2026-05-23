import { Link } from 'react-router';
import { useAuth } from '../context/AuthContext';
import { requireAuth } from '../lib/auth';

export function HydrateFallback() {
    return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">読み込み中...</div>;
}

export async function clientLoader(): Promise<null> {
    await requireAuth();
    return null;
}

export default function Account() {
    const { user, isLoading, logout } = useAuth();

    if (isLoading) {
        return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">読み込み中...</div>;
    }

    if (!user) {
        return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">読み込み中...</div>;
    }

    return (
        <div className="max-w-2xl mx-auto p-8">
            <h1 className="text-2xl font-bold mb-6">アカウント</h1>

            <div className="bg-white rounded-lg border border-gray-200 p-6 space-y-6">
                <section>
                    <h2 className="text-base font-semibold text-gray-700 mb-4">ユーザー情報</h2>
                    <dl className="space-y-4">
                        <div>
                            <dt className="text-sm font-medium text-gray-500">ユーザー名</dt>
                            <dd className="text-base text-gray-900">{user.profile.preferred_username ?? '-'}</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">メールアドレス</dt>
                            <dd className="text-base text-gray-900">{user.profile.email ?? '-'}</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">氏名</dt>
                            <dd className="text-base text-gray-900">
                                {user.profile.given_name && user.profile.family_name
                                    ? `${user.profile.given_name} ${user.profile.family_name}`
                                    : (user.profile.name ?? '-')}
                            </dd>
                        </div>
                    </dl>
                </section>

                <section className="pt-4 border-t border-gray-200">
                    <h2 className="text-base font-semibold text-gray-700 mb-4">認証情報</h2>
                    <dl className="space-y-4">
                        <div>
                            <dt className="text-sm font-medium text-gray-500">発行者</dt>
                            <dd className="text-base text-gray-900 break-all">{user.profile.iss}</dd>
                        </div>
                    </dl>
                </section>

                <section className="pt-4 border-t border-gray-200">
                    <h2 className="text-base font-semibold text-gray-700 mb-4">アカウント操作</h2>
                    <button
                        onClick={logout}
                        className="block w-full rounded bg-red-600 px-5 py-2 text-sm font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
                    >
                        ログアウト
                    </button>
                </section>
            </div>

            <div className="mt-6 text-center">
                <Link to="/settings" className="text-sm text-blue-600 hover:text-blue-700 hover:underline">
                    設定
                </Link>
            </div>
        </div>
    );
}
