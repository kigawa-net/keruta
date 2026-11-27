import { KiclDomain } from 'keruta-kicl-kicl-domain';
import { requireAuth } from '../lib/auth';

export function HydrateFallback() {
    return <div className="max-w-2xl mx-auto p-8 text-gray-500 text-sm">読み込み中...</div>;
}

export async function clientLoader(): Promise<null> {
    await requireAuth();
    return null;
}

export default function Index() {
    const version = KiclDomain.getInstance().VERSION;
    return (
        <div className="max-w-2xl mx-auto p-8">
            <h1 className="text-2xl font-bold mb-4">kicl</h1>
            <p className="text-sm text-gray-600">バージョン: {version}</p>
        </div>
    );
}
