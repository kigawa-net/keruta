import React, {useCallback, useState} from "react";
import {useWsState} from "../websocket/Websocket";
import FormTextInput, {InputValue} from "../form/FormTextInput";
import FormErrMsg from "../form/FormErrMsg";
import {ClientProviderAddTokenMsg, ServerProviderAddMsg} from "../../msg/provider";
import {useKerutaTaskState} from "../KerutaTask";
import {useNavigate} from "react-router-dom";
import {ProviderAddFormActions} from "./ProviderAddFormActions";
import {buildProviderAuthUrlFromMsg} from "./providerAuthUrl";
import {validateProviderForm} from "./providerValidation";
import {useProviderAddWebSocket, sendProviderAddMessage} from "./providerWebSocket";

type FormState = "inputting" | "fetching" | "submitting" | "redirecting"

type KerutaJson = {
    authorization_endpoint: string
    audience: string
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
    const [kerutaJson, setKerutaJson] = useState<KerutaJson | undefined>(undefined)
    const navigate = useNavigate()

    const handleWsReceive = useCallback((msg: ClientProviderAddTokenMsg) => {
        if (!kerutaJson) return
        const url = buildProviderAuthUrlFromMsg(
            kerutaJson.authorization_endpoint,
            kerutaJson.audience,
            msg,
        )
        setFormState("redirecting")
        navigate(url.toString())
    }, [kerutaJson, navigate])

    const handleSubmit = async () => {
        if (wsState.state != "open") {
            setErr("WebSocketが接続されていません")
            return
        }
        if (kerutaState.state != "connected" || kerutaState.auth.state != "authenticated") {
            setErr("認証されていません")
            return
        }

        const isValid = validateProviderForm({name, setName, issuer, setIssuer})
        if (!isValid) return

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
        if (!json.audience) {
            setErr("keruta.jsonにaudienceが含まれていません。ktcl-k8sの設定を確認してください。")
            setFormState("inputting")
            return
        }
        const msg: ServerProviderAddMsg = {
            type: "provider_add",
            name: name.value,
            issuer: issuerValue,
            audience: json.audience,
        }
        sendProviderAddMessage(wsState, msg)
        setFormState("submitting")
    }

    useProviderAddWebSocket(wsState, handleWsReceive)

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
            <ProviderAddFormActions isDisabled={isDisabled} buttonLabel={buttonLabel}/>
            <FormErrMsg err={err}/>
        </form>
    )
}
