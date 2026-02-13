import {Link} from "react-router";
import FormTextInput, {InputValue} from "../form/FormTextInput";
import {useEffect, useState} from "react";
import {useWsState} from "../websocket/Websocket";
import FormErrMsg from "../form/FormErrMsg";
import {ServerProviderCreateMsg} from "../../msg/provider";
import useWsReceive from "../websocket/useWsReceive";
import {useNavigate} from "react-router-dom";


interface IdpInputValue {
    issuer: InputValue
    subject: InputValue
    audience: InputValue
}

function emptyIdp(): IdpInputValue {
    return {
        issuer: {value: ""},
        subject: {value: ""},
        audience: {value: ""},
    }
}

export function ProviderCreateForm() {
    const [formState, setFormState] = useState<"inputting" | "submitting">("inputting")
    const ws = useWsState()
    const [name, setName] = useState<InputValue>({value: ""})
    const [issuer, setIssuer] = useState<InputValue>({value: ""})
    const [audience, setAudience] = useState<InputValue>({value: ""})
    const [idps, setIdps] = useState<IdpInputValue[]>([])
    const [err, setErr] = useState<string>()
    const navigate = useNavigate()

    const addIdp = () => setIdps(prev => [...prev, emptyIdp()])

    const removeIdp = (index: number) =>
        setIdps(prev => prev.filter((_, i) => i !== index))

    const updateIdp = (index: number, updated: Partial<IdpInputValue>) =>
        setIdps(prev => prev.map((idp, i) => i === index ? {...idp, ...updated} : idp))

    useEffect(() => {
        if (formState != "submitting") return
        if (ws.state != "open") {
            setErr("websocket is not connected")
            setFormState("inputting")
            return
        }
        if (name.value.trim() == "") {
            setName({error: "プロバイダー名を入力してください", ...name})
            setFormState("inputting")
            return;
        }
        if (issuer.value.trim() == "") {
            setIssuer({error: "Issuerを入力してください", ...issuer})
            setFormState("inputting")
            return;
        }
        if (audience.value.trim() == "") {
            setAudience({error: "Audienceを入力してください", ...audience})
            setFormState("inputting")
            return;
        }
        let idpValid = true
        const validatedIdps = idps.map(idp => {
            let updated = {...idp}
            if (idp.issuer.value.trim() == "") {
                updated = {...updated, issuer: {...idp.issuer, error: "Issuerを入力してください"}}
                idpValid = false
            }
            if (idp.subject.value.trim() == "") {
                updated = {...updated, subject: {...idp.subject, error: "Subjectを入力してください"}}
                idpValid = false
            }
            if (idp.audience.value.trim() == "") {
                updated = {...updated, audience: {...idp.audience, error: "Audienceを入力してください"}}
                idpValid = false
            }
            return updated
        })
        if (!idpValid) {
            setIdps(validatedIdps)
            setFormState("inputting")
            return
        }
        const msg: ServerProviderCreateMsg = {
            type: "provider_create",
            name: name.value,
            issuer: issuer.value,
            audience: audience.value,
            idps: idps.map(idp => ({
                issuer: idp.issuer.value,
                subject: idp.subject.value,
                audience: idp.audience.value,
            })),
        }
        ws.websocket.send(JSON.stringify(msg))
    }, [formState])
    useWsReceive(ws, msg => {
        if (msg.type != "provider_created") return
        navigate(`/provider`)
    }, [])
    return (
        <form className="space-y-6" onSubmit={(event) => {
            event.preventDefault()
            setFormState("submitting")
        }}>
            <input type="hidden" name="form_type" value={"provider_create"}/>
            <FormTextInput
                label={"プロバイダー名"} id={"name"} placeholder={"プロバイダー名を入力してください"} value={name}
                onChange={setName}
            />
            <FormTextInput
                label={"Issuer"} id={"issuer"} placeholder={"https://example.com/"} value={issuer}
                onChange={setIssuer}
            />
            <FormTextInput
                label={"Audience"} id={"audience"} placeholder={"audience-id"} value={audience}
                onChange={setAudience}
            />
            <div className="space-y-4">
                <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-gray-700">IDP</span>
                    <button
                        type="button"
                        onClick={addIdp}
                        className="px-3 py-1 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                    >
                        + IDP追加
                    </button>
                </div>
                {idps.map((idp, index) => (
                    <div key={index} className="border border-gray-200 rounded-lg p-4 space-y-3">
                        <div className="flex justify-between items-center">
                            <span className="text-sm font-medium text-gray-600">IDP {index + 1}</span>
                            <button
                                type="button"
                                onClick={() => removeIdp(index)}
                                className="text-sm text-red-600 hover:text-red-700"
                            >
                                削除
                            </button>
                        </div>
                        <FormTextInput
                            label={"Issuer"} id={`idp-issuer-${index}`} placeholder={"https://example.com/"}
                            value={idp.issuer}
                            onChange={v => updateIdp(index, {issuer: v})}
                        />
                        <FormTextInput
                            label={"Subject"} id={`idp-subject-${index}`} placeholder={"user-subject-id"}
                            value={idp.subject}
                            onChange={v => updateIdp(index, {subject: v})}
                        />
                        <FormTextInput
                            label={"Audience"} id={`idp-audience-${index}`} placeholder={"audience-id"}
                            value={idp.audience}
                            onChange={v => updateIdp(index, {audience: v})}
                        />
                    </div>
                ))}
            </div>
            <div className="flex gap-4">
                <button
                    type="submit"
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    disabled={formState == "submitting"}
                >
                    作成
                </button>
                <Link
                    type="button"
                    to={"/provider"}
                    className="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                >
                    キャンセル
                </Link>
            </div>
            <FormErrMsg err={err}/>
        </form>
    );
}
