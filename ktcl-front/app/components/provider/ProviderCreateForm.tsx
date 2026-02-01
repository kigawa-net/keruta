import {Link} from "react-router";
import FormTextInput, {InputValue} from "../form/FormTextInput";
import {useEffect, useState} from "react";
import {useWsState} from "../websocket/Websocket";
import FormErrMsg from "../form/FormErrMsg";
import {ServerProviderCreateMsg} from "../../msg/provider";
import useWsReceive from "../websocket/useWsReceive";
import {useNavigate} from "react-router-dom";


export function ProviderCreateForm() {
    const [formState, setFormState] = useState<"inputting" | "submitting">("inputting")
    const ws = useWsState()
    const [name, setName] = useState<InputValue>({value: ""})
    const [issuer, setIssuer] = useState<InputValue>({value: ""})
    const [audience, setAudience] = useState<InputValue>({value: ""})
    const [err, setErr] = useState<string>()
    const navigate = useNavigate()

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
        const msg: ServerProviderCreateMsg = {
            type: "provider_create",
            name: name.value,
            issuer: issuer.value,
            audience: audience.value
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
