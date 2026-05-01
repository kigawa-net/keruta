// noinspection JSUnusedGlobalSymbols
import {useWebsocketReceive} from "../util/net/websocket/useWebsocketReceive";
import {useEffect, useState} from "react";
import {ClientProviderListMsg, ServerProviderDeleteMsg, ServerProviderListMsg} from "../components/msg/provider";

import {Link} from "react-router";
import {useWebsocketState} from "../util/net/websocket/WebsocketProvider";
import {useAuthedKtseState} from "../components/api/AuthedKtseProvider";
import {ProviderTable} from "../components/provider/ProviderTable";
import {ProviderCardList} from "../components/provider/ProviderCardList";
import {ProviderPresetSection} from "../components/provider/ProviderPresetSection";

type Provider = ClientProviderListMsg["providers"][0]

// noinspection JSUnusedGlobalSymbols
export default function AboutRoute() {
    const wsState = useWebsocketState()
    const authedKtse = useAuthedKtseState()
    const [providers, setProviders] = useState<Provider[]>()
    useWebsocketReceive(msg => {
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
                .catch(() => {
                    console.error(`Failed to fetch auth endpoint for issuer: ${issuer}`)
                })
        })
    }, [providers])
    useEffect(() => {
        if (wsState.state != "open") return
        if (authedKtse.state != "loaded") return;
        const msg: ServerProviderListMsg = {
            type: "provider_list"
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [wsState.state, authedKtse.state]);
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
        <div className="max-w-6xl mx-auto p-3 md:p-6">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6 md:mb-8">
                <h1 className="text-2xl md:text-3xl font-bold">プロバイダー一覧</h1>
                <Link
                    to="/provider/add"
                    className="w-full sm:w-auto px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-center"
                >
                    プロバイダーを追加
                </Link>
            </div>
            <ProviderPresetSection/>
            <ProviderTable providers={providers} onDelete={handleDelete}/>
            <ProviderCardList providers={providers} onDelete={handleDelete}/>
        </div>
    )
}
