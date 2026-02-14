// noinspection JSUnusedGlobalSymbols
import {Link} from "react-router";
import {useState} from "react";
import {useWsState} from "../components/websocket/Websocket";
import useWsReceive from "../components/websocket/useWsReceive";
import FormTextInput, {InputValue} from "../components/form/FormTextInput";
import FormErrMsg from "../components/form/FormErrMsg";
import {ServerProviderAddMsg} from "../msg/provider";
import {useKerutaTaskState} from "../components/KerutaTask";

type FormState = "inputting" | "fetching" | "submitting" | "redirecting"

type KerutaJson = {
    authorization_endpoint: string
    audience: string
}

// noinspection JSUnusedGlobalSymbols
export default function ProviderAddRoute() {
    const wsState = useWsState()
    const kerutaState = useKerutaTaskState()
    const [formState, setFormState] = useState<FormState>("inputting")
    const [name, setName] = useState<InputValue>({value: ""})
    const [issuer, setIssuer] = useState<InputValue>({value: ""})
    const [err, setErr] = useState<string>()
    const [kerutaJson, setKerutaJson] = useState<KerutaJson>()

    const handleSubmit = async () => {
        if (wsState.state != "open") {
            setErr("WebSocketが接続されていません")
            return
        }
        if (kerutaState.state != "connected" || kerutaState.auth.state != "authenticated") {
            setErr("認証されていません")
            return
        }
        if (name.value.trim() == "") {
            setName({...name, error: "名前を入力してください"})
            return
        }
        if (issuer.value.trim() == "") {
            setIssuer({...issuer, error: "Issuerを入力してください"})
            return
        }

        setFormState("fetching")
        const issuerValue = issuer.value.replace(/\/$/, "")
        let json: KerutaJson
        try {
            const res = await fetch(`${issuerValue}/.well-known/keruta.json`)
            json = await res.json()
        } catch {
            setErr("keruta.jsonの取得に失敗しました")
            setFormState("inputting")
            return
        }

        setKerutaJson(json)
        const msg: ServerProviderAddMsg = {
            type: "provider_add",
            name: name.value,
            issuer: issuerValue,
            audience: json.audience,
        }
        wsState.websocket.send(JSON.stringify(msg))
        setFormState("submitting")
    }

    useWsReceive(wsState, msg => {
        if (msg.type != "provider_add_token_issued") return
        if (!kerutaJson) return
        const url = new URL(kerutaJson.authorization_endpoint)
        url.searchParams.set("state", msg.token)
        url.searchParams.set("redirect_uri", `${window.location.origin}/provider/complete`)
        url.searchParams.set("client_id", kerutaJson.audience)
        url.searchParams.set("response_type", "code")
        url.searchParams.set("scope", "openid")
        setFormState("redirecting")
        window.location.href = url.toString()
    }, [kerutaJson])

    const isDisabled = formState != "inputting"
    const buttonLabel = formState == "redirecting" ? "リダイレクト中..." : formState == "fetching" || formState == "submitting" ? "処理中..." : "追加"

    return (
        <div className="max-w-2xl mx-auto p-6">
            <div className="flex items-center gap-4 mb-8">
                <Link to="/provider" className="text-gray-500 hover:text-gray-700">← 戻る</Link>
                <h1 className="text-3xl font-bold">プロバイダーを追加</h1>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
                <form className="space-y-6" onSubmit={(e) => {
                    e.preventDefault()
                    void handleSubmit()
                }}>
                    <FormTextInput
                        label="名前" id="name" placeholder="プロバイダーの名前"
                        value={name} onChange={setName}
                    />
                    <FormTextInput
                        label="Issuer" id="issuer" placeholder="https://provider.example.com"
                        value={issuer} onChange={setIssuer}
                    />
                    <div className="flex gap-4">
                        <button
                            type="submit"
                            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
                            disabled={isDisabled}
                        >
                            {buttonLabel}
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
