import {ClientProviderListMsg} from "../msg/provider";

type Provider = ClientProviderListMsg["providers"][0]

type Props = {
    providers: Provider[] | undefined
    onDelete: (id: string) => void
}

export function ProviderTable({providers, onDelete}: Props) {
    return (
        <div className="hidden md:block bg-white rounded-lg shadow overflow-x-auto">
            <table className="w-full">
                <thead className="bg-gray-50 border-b">
                <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">名前</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Issuer</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Audience</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">IDP</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
                </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                {providers?.map(p => (
                    <tr key={p.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{p.name}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            <a href={p.issuer} className={"text-blue-600 hover:underline "}>{p.issuer}</a>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{p.audience}</td>
                        <td className="px-6 py-4 text-sm text-gray-500">
                            {p.idps.length === 0 ? (
                                <span className="text-gray-400">なし</span>
                            ) : (
                                <ul className="space-y-1">
                                    {p.idps.map((idp, i) => (
                                        <li key={i} className="text-xs">
                                            <p
                                                rel="noopener noreferrer"
                                                className="font-medium"
                                            >
                                                {idp.issuer}
                                            </p>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                            <button
                                onClick={() => onDelete(p.id)}
                                className="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700 transition-colors"
                            >
                                削除
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {(!providers || providers.length === 0) && (
                <div className="text-center py-12 text-gray-500">
                    プロバイダーが登録されていません
                </div>
            )}
        </div>
    )
}
