import {Form, useActionData, useLoaderData} from 'react-router';
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
        </div>
    );
}

function Field({
    label,
    name,
    defaultValue,
    placeholder,
}: {
    label: string;
    name: string;
    defaultValue: string;
    placeholder: string;
}) {
    return (
        <label className="block">
            <span className="block text-sm font-medium text-gray-600 mb-1">{label}</span>
            <input
                name={name}
                type="url"
                defaultValue={defaultValue}
                placeholder={placeholder}
                className="block w-full rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
            />
        </label>
    );
}
