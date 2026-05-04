import {Form, useLoaderData, useNavigation} from 'react-router';
import type {KeycloakProfile} from 'keycloak-js';

export function HydrateFallback() {
    return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">読み込み中...</div>;
}

interface AccountData {
    userProfile: KeycloakProfile | null;
    keycloakUrl: string;
    keycloakRealm: string;
}

// Todo: サーバーサイドでKeycloak設定を読み込む実装に置き換え
export async function clientLoader(): Promise<AccountData> {
    // ブラウザサイドではlocalStorageまたはwindowからKeycloak設定を読み込む
    const keycloakUrl = localStorage.getItem('keycloakUrl') ?? 'https://id.example.com';
    const keycloakRealm = localStorage.getItem('keycloakRealm') ?? 'kigawa-net';

    return {
        userProfile: null, // Todo: 実際のKeycloakクライアントで読み込み
        keycloakUrl,
        keycloakRealm,
    };
}

export async function clientAction({request}: {request: Request}): Promise<{success: boolean}> {
    const formData = await request.formData();
    const action = formData.get('_action');

    if (action === 'logout') {
        // Todo: Keycloakログアウトを実装
        // ログアウト後にOIDCログエリダイレクトURIへリダイレクト
        const redirectUri = window.location.origin;
        window.location.href = `${redirectUri}/`;
        return {success: true};
    }

    return {success: true};
}

export default function Account() {
    const {userProfile, keycloakRealm} = useLoaderData() as AccountData;
    const navigation = useNavigation();
    const isSubmitting = navigation.state === 'submitting';

    return (
        <div className="max-w-2xl mx-auto p-8">
            <h1 className="text-2xl font-bold mb-6">アカウント</h1>

            <div className="bg-white rounded-lg border border-gray-200 p-6 space-y-6">
                <section>
                    <h2 className="text-base font-semibold text-gray-700 mb-4">ユーザー情報</h2>
                    {userProfile ? (
                        <dl className="space-y-4">
                            <div>
                                <dt className="text-sm font-medium text-gray-500">ユーザー名</dt>
                                <dd className="text-base text-gray-900">{userProfile.username ?? '-'}</dd>
                            </div>
                            <div>
                                <dt className="text-sm font-medium text-gray-500">メールアドレス</dt>
                                <dd className="text-base text-gray-900">{userProfile.email ?? '-'}</dd>
                            </div>
                            <div>
                                <dt className="text-sm font-medium text-gray-500">氏名</dt>
                                <dd className="text-base text-gray-900">
                                    {userProfile.firstName && userProfile.lastName
                                        ? `${userProfile.firstName} ${userProfile.lastName}`
                                        : userProfile.firstName ?? userProfile.lastName ?? '-'}
                                </dd>
                            </div>
                        </dl>
                    ) : (
                        <p className="text-sm text-gray-500">ユーザー情報がありません</p>
                    )}
                </section>

                <section className="pt-4 border-t border-gray-200">
                    <h2 className="text-base font-semibold text-gray-700 mb-4">認証情報</h2>
                    <dl className="space-y-4">
                        <div>
                            <dt className="text-sm font-medium text-gray-500">認証プロバイダー</dt>
                            <dd className="text-base text-gray-900">Keycloak</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Realm</dt>
                            <dd className="text-base text-gray-900">{keycloakRealm}</dd>
                        </div>
                    </dl>
                </section>

                <section className="pt-4 border-t border-gray-200">
                    <h2 className="text-base font-semibold text-gray-700 mb-4">アカウント操作</h2>
                    <Form method="post">
                        <input type="hidden" name="_action" value="logout" />
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="block w-full rounded bg-red-600 px-5 py-2 text-sm font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
                        >
                            {isSubmitting ? 'ログアウト中...' : 'ログアウト'}
                        </button>
                    </Form>
                </section>
            </div>

            <div className="mt-6 text-center">
                <a href="/settings" className="text-sm text-blue-600 hover:text-blue-700 hover:underline">
                    設定に戻る
                </a>
            </div>
        </div>
    );
}