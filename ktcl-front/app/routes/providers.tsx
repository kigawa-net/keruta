// noinspection JSUnusedGlobalSymbols
import useWsReceive from "../components/websocket/useWsReceive";
import {useWsState} from "../components/websocket/Websocket";
import {useEffect, useState} from "react";
import {ClientProviderListMsg, ServerProviderDeleteMsg, ServerProviderListMsg} from "../msg/provider";
import {useKerutaTaskState} from "../components/KerutaTask";
import {Link} from "react-router";

type Provider = ClientProviderListMsg["providers"][0]

function buildOidcLoginUrl(authorizationEndpoint: string, clientId: string): string {
    const url = new URL(authorizationEndpoint)
    url.searchParams.set("response_type", "code")
    url.searchParams.set("client_id", clientId)
    url.searchParams.set("redirect_uri", window.location.origin)
    url.searchParams.set("scope", "openid")
    return url.toString()
}

export default function AboutRoute() {
    const wsState = useWsState()
    const [providers, setProviders] = useState<Provider[]>()
    const [authEndpoints, setAuthEndpoints] = useState<Record<string, string>>({})
    const kerutaState = useKerutaTaskState()
    useWsReceive(wsState, msg => {
        if (msg.type == "provider_listed") {
            setProviders(msg.providers)
        } else if (msg.type == "provider_deleted") {
            setProviders(prev => prev?.filter(p => p.id !== msg.id))
        }
    }, [])
    useEffect(() => {
        if (!providers) return
        const issuers = Array.from(new Set(providers.flatMap(p => p.idps.map(idp => idp.issuer))))
        issuers.forEach(issuer => {
            fetch(`${issuer}/.well-known/openid-configuration`)
                .then(res => res.json())
                .then((data: {authorization_endpoint: string}) => {
                    setAuthEndpoints(prev => ({...prev, [issuer]: data.authorization_endpoint}))
                })
                .catch(() => {})
        })
    }, [providers])
    useEffect(() => {
        if (wsState.state != "open") return
        if (kerutaState.state != "connected") return;
        if (kerutaState.auth.state != "authenticated") return;
        const msg: ServerProviderListMsg = {
            type: "provider_list"
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [wsState.state, kerutaState.state == "connected" && kerutaState.auth.state]);
    const handleDelete = (id: string) => {
        if (!confirm("このプロバイダーを削除しますか？")) return
        if (wsState.state != "open") return
        const msg: ServerProviderDeleteMsg = {
            type: "provider_delete",
            id,
        }
        wsState.websocket.send(JSON.stringify(msg))
    }

    return (
        <div className="max-w-6xl mx-auto p-6">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold">プロバイダー一覧</h1>
                <Link
                    to="/provider/add"
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                    プロバイダーを追加
                </Link>
            </div>
            <div className="bg-white rounded-lg shadow">
                <table className="w-full">
                    <thead className="bg-gray-50 border-b">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
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
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{p.id}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{p.name}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{p.issuer}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{p.audience}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">
                                    {p.idps.length === 0 ? (
                                        <span className="text-gray-400">なし</span>
                                    ) : (
                                        <ul className="space-y-1">
                                            {p.idps.map((idp, i) => (
                                                <li key={i} className="text-xs">
                                                    <a
                                                        href={authEndpoints[idp.issuer]
                                                            ? buildOidcLoginUrl(authEndpoints[idp.issuer], idp.audience)
                                                            : idp.issuer}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                        className="text-blue-600 hover:underline font-medium"
                                                    >
                                                        {idp.issuer}
                                                    </a>
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm">
                                    <button
                                        onClick={() => handleDelete(p.id)}
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
        </div>
    )
}
