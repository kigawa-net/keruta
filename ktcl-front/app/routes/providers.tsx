// noinspection JSUnusedGlobalSymbols
import useWsReceive from "../components/websocket/useWsReceive";
import {useWsState} from "../components/websocket/Websocket";
import {useEffect, useState} from "react";
import {ClientProviderListMsg, ServerProviderListMsg} from "../msg/provider";
import {useKerutaTaskState} from "../components/KerutaTask";
import {Link} from "react-router-dom";

type Provider = ClientProviderListMsg["providers"][0]
export default function AboutRoute() {
    const wsState = useWsState()
    const [providers, setProviders] = useState<Provider[]>()
    const kerutaState = useKerutaTaskState()
    useWsReceive(wsState, msg => {
        if (msg.type != "provider_listed") return
        setProviders(msg.providers)
    }, [])
    useEffect(() => {
        if (wsState.state != "open") return
        if (kerutaState.state != "connected") return;
        if (kerutaState.auth.state != "authenticated") return;
        const msg: ServerProviderListMsg = {
            type: "provider_list"
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [wsState.state, kerutaState.state == "connected" && kerutaState.auth.state]);
    return (
        <div className="max-w-6xl mx-auto p-6">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold">プロバイダー一覧</h1>
                <Link
                    to="/provider/create"
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                    プロバイダー追加
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
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {providers?.map(p => (
                            <tr key={p.id} className="hover:bg-gray-50">
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{p.id}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{p.name}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{p.issuer}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{p.audience}</td>
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
