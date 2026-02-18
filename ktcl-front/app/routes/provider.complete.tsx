// noinspection JSUnusedGlobalSymbols
import { useNavigate, useSearchParams } from "react-router";
import { useEffect, useState } from "react";
import { useWsState } from "../components/service/useServiceHooks";
import useWsReceive from "../components/net/websocket/useWsReceive";
import { ServerProviderCompleteMsg } from "../components/msg/provider";
import { useKerutaTaskState } from "../components/app/AppContext";

type Status = "processing" | "success" | "error"

export default function ProviderCompleteRoute() {
    const [searchParams] = useSearchParams()
    const wsState = useWsState()
    const kerutaState = useKerutaTaskState()
    const navigate = useNavigate()
    const [status, setStatus] = useState<Status>("processing")
    const [errMsg, setErrMsg] = useState<string>()

    const code = searchParams.get("code")
    const token = searchParams.get("state")

    useEffect(() => {
        if (!code || !token) {
            setStatus("error")
            setErrMsg("無効なコールバックパラメータです")
            return
        }
        if (wsState.state != "open") return
        if (kerutaState.state != "connected") return
        if (kerutaState.auth.state != "authenticated") return

        const redirectUri = `${window.location.origin}/provider/complete`
        const msg: ServerProviderCompleteMsg = {
            type: "provider_complete",
            token,
            code,
            redirectUri,
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [wsState.state, kerutaState.state == "connected" && kerutaState.auth.state])

    useWsReceive(wsState, msg => {
        if (msg.type != "provider_idp_added") return
        setStatus("success")
        setTimeout(() => navigate("/provider"), 1500)
    }, [])

    return (
        <div className="max-w-lg mx-auto p-6 mt-16 text-center">
            {status == "processing" && (
                <div>
                    <div className="text-4xl mb-4">⏳</div>
                    <h1 className="text-2xl font-bold mb-2">プロバイダーを登録中...</h1>
                    <p className="text-gray-500">しばらくお待ちください</p>
                </div>
            )}
            {status == "success" && (
                <div>
                    <div className="text-4xl mb-4">✓</div>
                    <h1 className="text-2xl font-bold mb-2 text-green-600">登録完了</h1>
                    <p className="text-gray-500">プロバイダー一覧に移動します...</p>
                </div>
            )}
            {status == "error" && (
                <div>
                    <div className="text-4xl mb-4">✗</div>
                    <h1 className="text-2xl font-bold mb-2 text-red-600">エラー</h1>
                    <p className="text-gray-500 mb-4">{errMsg ?? "プロバイダーの登録に失敗しました"}</p>
                    <button
                        onClick={() => navigate("/provider")}
                        className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        プロバイダー一覧に戻る
                    </button>
                </div>
            )}
        </div>
    )
}
