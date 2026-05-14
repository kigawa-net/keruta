import {Form, useActionData, useLoaderData} from 'react-router';
import {useState} from 'react';
import {type AppSettings, defaultSettings, SETTINGS_STORAGE_KEY} from '../types/settings';

export async function clientLoader(): Promise<AppSettings> {
    const stored = localStorage.getItem(SETTINGS_STORAGE_KEY);
    if (!stored) return defaultSettings;
    return {...defaultSettings, ...(JSON.parse(stored) as Partial<AppSettings>)};
}

export async function clientAction({request}: { request: Request }): Promise<{ saved: boolean }> {
    const formData = await request.formData();
    const settings: AppSettings = {
        ownIssuerUrl: String(formData.get('ownIssuerUrl') ?? ''),
        userIssuerUrl: String(formData.get('userIssuerUrl') ?? ''),
        oidcClientId: String(formData.get('oidcClientId') ?? ''),
        ktseUrl: String(formData.get('ktseUrl') ?? ''),
        language: (String(formData.get('language')) as AppSettings['language']) || 'ja',
    };
    localStorage.setItem(SETTINGS_STORAGE_KEY, JSON.stringify(settings));
    return {saved: true};
}

export function HydrateFallback() {
    return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">読み込み中...</div>;
}

export default function Settings() {
    const settings = useLoaderData() as AppSettings;
    const actionData = useActionData() as { saved: boolean } | undefined;
    const [registerStatus, setRegisterStatus] = useState<{success: boolean; message: string} | null>(null);
    const [isRegistering, setIsRegistering] = useState(false);

    const handleKicpRegister = async () => {
        setIsRegistering(true);
        setRegisterStatus(null);

        try {
            const form = document.querySelector('form[method="post"]')?.closest('div')?.querySelector('section:last-of-type');
            const oidcTokenInput = form?.querySelector('input[name="oidcToken"]') as HTMLInputElement;
            const providerTokenInput = form?.querySelector('input[name="providerToken"]') as HTMLInputElement;
            const registerTokenInput = form?.querySelector('input[name="registerToken"]') as HTMLInputElement;

            const oidcToken = oidcTokenInput?.value || '';
            const providerToken = providerTokenInput?.value || '';
            const registerToken = registerTokenInput?.value || '';

            if (!oidcToken || !providerToken || !registerToken) {
                setRegisterStatus({success: false, message: 'すべてのトークンを入力してください。'});
                return;
            }

            if (!settings.ktseUrl) {
                setRegisterStatus({success: false, message: 'タスクサーバーURLが設定されていません。'});
                return;
            }

            const registerUrl = `${settings.ktseUrl}/api/kicp/register`;

            console.log('kicpクライアント登録リクエスト送信:', {
                url: registerUrl,
                oidcToken: oidcToken.substring(0, 20) + '...',
                hasProviderToken: !!providerToken,
                hasRegisterToken: !!registerToken,
            });

            const response = await fetch(registerUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    oidcToken,
                    providerToken,
                    registerToken,
                }),
            });

            if (response.ok) {
                const data = await response.json().catch(() => ({ message: 'レスポンスの解析に失敗しました' }));
                setRegisterStatus({success: true, message: `登録成功: ${JSON.stringify(data)}`});
            } else {
                const errorText = await response.text().catch(() => response.statusText);
                setRegisterStatus({success: false, message: `登録失敗: ${response.status} ${errorText}`});
            }
        } catch (error) {
            console.error('kicp登録エラー:', error);
            setRegisterStatus({success: false, message: `エラー: ${error instanceof Error ? error.message : String(error)}`});
        } finally {
            setIsRegistering(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto p-8">
            <h1 className="text-2xl font-bold mb-6">設定</h1>

            {actionData?.saved && (
                <div className="mb-6 rounded bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700">
                    設定を保存しました。
                </div>
            )}

            <Form method="post" className="space-y-8">
                <section>
                    <h2 className="text-base font-semibold text-gray-700 mb-4">接続設定</h2>
                    <div className="space-y-4">
                        <Field
                            label="IDサーバー Issuer URL"
                            name="ownIssuerUrl"
                            defaultValue={settings.ownIssuerUrl}
                            placeholder="https://id.example.com"
                        />
                        <Field
                            label="OIDCプロバイダー URL"
                            name="userIssuerUrl"
                            defaultValue={settings.userIssuerUrl}
                            placeholder="https://oidc.example.com"
                        />
                        <Field
                            label="OIDCクライアント ID"
                            name="oidcClientId"
                            defaultValue={settings.oidcClientId}
                            placeholder="my-client"
                        />
                        <Field
                            label="タスクサーバー URL"
                            name="ktseUrl"
                            defaultValue={settings.ktseUrl}
                            placeholder="https://task.example.com"
                        />
                    </div>
                </section>

                <section>
                    <h2 className="text-base font-semibold text-gray-700 mb-4">表示設定</h2>
                    <label className="block">
                        <span className="block text-sm font-medium text-gray-600 mb-1">言語</span>
                        <select
                            name="language"
                            defaultValue={settings.language}
                            className="block w-full rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
                        >
                            <option value="ja">日本語</option>
                            <option value="en">English</option>
                        </select>
                    </label>
                </section>

                <button
                    type="submit"
                    className="rounded bg-blue-600 px-5 py-2 text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    保存
                </button>
            </Form>

            {/* kicpクライアント登録セクション */}
            <section className="mt-12 pt-8 border-t border-gray-200">
                <h2 className="text-base font-semibold text-gray-700 mb-4">kicpクライアント登録</h2>
                <p className="text-sm text-gray-600 mb-4">
                    kicpクライアントを登録して、クロスドメイン認証を有効化します。
                </p>

                {registerStatus && (
                    <div className={`mb-4 rounded border px-4 py-3 text-sm ${
                        registerStatus.success 
                            ? 'bg-green-50 border-green-200 text-green-700' 
                            : 'bg-red-50 border-red-200 text-red-700'
                    }`}>
                        {registerStatus.message}
                    </div>
                )}

                <div className="space-y-4">
                    <Field
                        label="OIDCトークン"
                        name="oidcToken"
                        defaultValue={localStorage.getItem('oidc_token') || ''}
                        placeholder="OIDCトークンを入力"
                        type="text"
                    />
                    <Field
                        label="プロバイダートークン"
                        name="providerToken"
                        defaultValue={localStorage.getItem('provider_token') || ''}
                        placeholder="プロバイダートークンを入力"
                        type="text"
                    />
                    <Field
                        label="登録トークン"
                        name="registerToken"
                        defaultValue={localStorage.getItem('register_token') || ''}
                        placeholder="登録トークンを入力"
                        type="text"
                    />
                </div>

                <button
                    type="button"
                    onClick={handleKicpRegister}
                    disabled={isRegistering}
                    className="mt-4 rounded bg-green-600 px-5 py-2 text-sm font-medium text-white hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 disabled:opacity-50"
                >
                    {isRegistering ? '登録中...' : 'kicpクライアントを登録'}
                </button>
            </section>
        </div>
    );
}

function Field({
    label,
    name,
    defaultValue,
    placeholder,
    type = 'text',
}: {
    label: string;
    name: string;
    defaultValue: string;
    placeholder: string;
    type?: string;
}) {
    return (
        <label className="block">
            <span className="block text-sm font-medium text-gray-600 mb-1">{label}</span>
            <input
                name={name}
                type={type}
                defaultValue={defaultValue}
                placeholder={placeholder}
                className="block w-full rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            />
        </label>
    );
}
