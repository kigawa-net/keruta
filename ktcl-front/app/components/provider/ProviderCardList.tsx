import {ClientProviderListMsg} from "../msg/provider";

type Provider = ClientProviderListMsg["providers"][0]

type Props = {
    providers: Provider[] | undefined
    onDelete: (id: string) => void
}

export function ProviderCardList({providers, onDelete}: Props) {
    return (
        <div className="md:hidden space-y-4">
            {providers?.map(p => (
                <div key={p.id} className="bg-white rounded-lg shadow border border-gray-200 p-4">
                    <div className="flex justify-between items-start mb-3">
                        <div>
                            <h3 className="text-base font-medium text-gray-900">{p.name}</h3>
                            <p className="text-sm text-gray-500">ID: {p.id}</p>
                        </div>
                        <button
                            onClick={() => onDelete(p.id)}
                            className="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700 transition-colors text-sm"
                        >
                            削除
                        </button>
                    </div>
                    <div className="space-y-2 text-sm">
                        <div>
                            <span className="text-gray-500">Issuer: </span>
                            <span className="text-gray-900 break-all"><a href={p.issuer}
                                                                         className={"text-blue-600 hover:underline "}>{p.issuer}</a></span>
                        </div>
                        <div>
                            <span className="text-gray-500">Audience: </span>
                            <span className="text-gray-900">{p.audience}</span>
                        </div>
                        <div>
                            <span className="text-gray-500">IDP: </span>
                            {p.idps.length === 0 ? (
                                <span className="text-gray-400">なし</span>
                            ) : (
                                <ul className="mt-1 space-y-1">
                                    {p.idps.map((idp, i) => (
                                        <li key={i}>
                                            <p
                                                rel="noopener noreferrer"
                                            >
                                                {idp.issuer}
                                            </p>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    </div>
                </div>
            ))}
            {(!providers || providers.length === 0) && (
                <div className="text-center py-12 text-gray-500 bg-white rounded-lg shadow">
                    プロバイダーが登録されていません
                </div>
            )}
        </div>
    )
}
