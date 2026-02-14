import React, {useRef, useState} from "react";
import {Link} from "react-router";
import {useWsState} from "../websocket/Websocket";
import useWsReceive from "../websocket/useWsReceive";
import FormTextInput, {InputValue} from "../form/FormTextInput";
import FormErrMsg from "../form/FormErrMsg";
import {ServerProviderAddMsg} from "../../msg/provider";
import {useKerutaTaskState} from "../KerutaTask";
import {ReceiveMsg} from "../../msg/msg";
import {NavigateFunction, useNavigate} from "react-router-dom";

type FormState = "inputting" | "fetching" | "submitting" | "redirecting"

type KerutaJson = {
    authorization_endpoint: string
    audience: string
}

function handleWsReceive(
    msg: ReceiveMsg,
    kerutaJsonRef: React.RefObject<KerutaJson | undefined>,
    setFormState: React.Dispatch<React.SetStateAction<FormState>>,
    navigate: NavigateFunction,
) {
    if (msg.type != "provider_add_token_issued") return
    if (!kerutaJsonRef.current) return
    const url = new URL(kerutaJsonRef.current.authorization_endpoint)
    url.searchParams.set("state", msg.token)
    url.searchParams.set("redirect_uri", `${window.location.origin}/provider/complete`)
    url.searchParams.set("client_id", kerutaJsonRef.current.audience)
    url.searchParams.set("response_type", "code")
    url.searchParams.set("scope", "openid")
    setFormState("redirecting")
    navigate(url.toString())
}

function handleFormSubmit(e: React.FormEvent, onSubmit: () => void) {
    e.preventDefault()
    void onSubmit()
}

export function ProviderAddForm() {
    const wsState = useWsState()
    const kerutaState = useKerutaTaskState()
    const [formState, setFormState] = useState<FormState>("inputting")
    const [name, setName] = useState<InputValue>({value: ""})
    const [issuer, setIssuer] = useState<InputValue>({value: ""})
    const [err, setErr] = useState<string>()
    const kerutaJsonRef = useRef<KerutaJson | undefined>(undefined)
    const navigate = useNavigate()

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

        kerutaJsonRef.current = json
        const msg: ServerProviderAddMsg = {
            type: "provider_add",
            name: name.value,
            issuer: issuerValue,
            audience: json.audience,
        }
        wsState.websocket.send(JSON.stringify(msg))
        setFormState("submitting")
    }

    useWsReceive(wsState, (msg) => handleWsReceive(msg, kerutaJsonRef, setFormState, navigate), [])

    const isDisabled = formState != "inputting"
    const buttonLabel = formState == "redirecting" ? "リダイレクト中..." : formState == "fetching" || formState == "submitting" ? "処理中..." : "追加"

    return (
        <form className="space-y-6" onSubmit={(e) => handleFormSubmit(e, handleSubmit)}>
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
    )
}
