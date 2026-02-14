// noinspection JSUnusedGlobalSymbols
import {Link, useNavigate} from "react-router";
import {useEffect, useState} from "react";
import {useWsState} from "../components/websocket/Websocket";
import useWsReceive from "../components/websocket/useWsReceive";
import FormTextInput, {InputValue} from "../components/form/FormTextInput";
import FormErrMsg from "../components/form/FormErrMsg";
import {ServerProviderAddMsg} from "../msg/provider";
import {useKerutaTaskState} from "../components/KerutaTask";

type FormState = "inputting" | "submitting" | "redirecting"

// noinspection JSUnusedGlobalSymbols
export default function ProviderAddRoute() {
    const wsState = useWsState()
    const kerutaState = useKerutaTaskState()
    const [formState, setFormState] = useState<FormState>("inputting")
    const [name, setName] = useState<InputValue>({value: ""})
    const [issuer, setIssuer] = useState<InputValue>({value: ""})
    const [audience, setAudience] = useState<InputValue>({value: ""})
    const [err, setErr] = useState<string>()

    useEffect(() => {
        if (formState != "submitting") return
        if (wsState.state != "open") {
            setErr("WebSocketが接続されていません")
            setFormState("inputting")
            return
        }
        if (kerutaState.state != "connected" || kerutaState.auth.state != "authenticated") {
            setErr("認証されていません")
            setFormState("inputting")
            return
        }
        if (name.value.trim() == "") {
            setName({...name, error: "名前を入力してください"})
            setFormState("inputting")
            return
        }
        if (issuer.value.trim() == "") {
            setIssuer({...issuer, error: "Issuerを入力してください"})
            setFormState("inputting")
            return
        }
        if (audience.value.trim() == "") {
            setAudience({...audience, error: "Audienceを入力してください"})
            setFormState("inputting")
            return
        }
        const msg: ServerProviderAddMsg = {
            type: "provider_add",
            name: name.value,
            issuer: issuer.value,
            audience: audience.value,
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [formState])

    useWsReceive(wsState, async msg => {
        if (msg.type != "provider_add_token_issued") return
        const token = msg.token
        const issuerValue = issuer.value.replace(/\/$/, "")

        let authorizationEndpoint: string
        try {
            const res = await fetch(`${issuerValue}/.well-known/keruta.json`)
            const kerutaJson: {authorization_endpoint: string} = await res.json()
            authorizationEndpoint = kerutaJson.authorization_endpoint
        } catch {
            setErr("keruta.jsonの取得に失敗しました")
            setFormState("inputting")
            return
        }

        const redirectUri = `${window.location.origin}/provider/complete`
        const url = new URL(authorizationEndpoint)
        url.searchParams.set("state", token)
        url.searchParams.set("redirect_uri", redirectUri)
        url.searchParams.set("client_id", audience.value)
        url.searchParams.set("response_type", "code")
        url.searchParams.set("scope", "openid")

        setFormState("redirecting")
        window.location.href = url.toString()
    }, [issuer.value, audience.value])

    return (
        <div className="max-w-2xl mx-auto p-6">
            <div className="flex items-center gap-4 mb-8">
                <Link to="/provider" className="text-gray-500 hover:text-gray-700">← 戻る</Link>
                <h1 className="text-3xl font-bold">プロバイダーを追加</h1>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
                <form className="space-y-6" onSubmit={(e) => {
                    e.preventDefault()
                    setFormState("submitting")
                }}>
                    <FormTextInput
                        label="名前" id="name" placeholder="プロバイダーの名前"
                        value={name} onChange={setName}
                    />
                    <FormTextInput
                        label="Issuer" id="issuer" placeholder="https://provider.example.com"
                        value={issuer} onChange={setIssuer}
                    />
                    <FormTextInput
                        label="Audience" id="audience" placeholder="client-id"
                        value={audience} onChange={setAudience}
                    />
                    <div className="flex gap-4">
                        <button
                            type="submit"
                            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
                            disabled={formState != "inputting"}
                        >
                            {formState == "redirecting" ? "リダイレクト中..." : formState == "submitting" ? "処理中..." : "追加"}
                        </button>
                        <Link
                            to="/provider"
                            className="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                        >
                            キャンセル
                        </Link>
                    </div>
                    <FormErrMsg err={err}/>
                </form>
            </div>
        </div>
    )
}
