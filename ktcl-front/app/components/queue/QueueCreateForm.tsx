import {Link} from "react-router";
import FormTextInput, {InputValue} from "../form/FormTextInput";
import {useEffect, useState} from "react";
import {useWsState} from "../websocket/Websocket";
import FormErrMsg from "../form/FormErrMsg";
import {ServerQueueCreateMsg} from "../../msg/queue";
import useWsReceive from "../websocket/useWsReceive";
import {useNavigate} from "react-router-dom";
import FormProviderInput from "../form/FormProviderInput";


export function QueueCreateForm() {
    const [formState, setFormState] = useState<"inputting" | "submitting">("inputting")
    const ws = useWsState()
    const [provider, setProvider] = useState<InputValue>({value: ""})
    const [name, setName] = useState<InputValue>({value: ""})
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
            setName({error: "タスク名を入力してください", ...name})
            setFormState("inputting")
            return;
        }
        const msg: ServerQueueCreateMsg = {
            type: "queue_create",
            name: name.value,
            providerId: parseInt(provider.value)
        }
        ws.websocket.send(JSON.stringify(msg))
    }, [formState])
    useWsReceive(ws, msg => {
        if (msg.type != "queue_created") return
        navigate(`/queue/${msg.queueId}`)
    }, [])
    return (
        <form className="space-y-6" onSubmit={(event) => {
            event.preventDefault()
            setFormState("submitting")
        }}>
            <input type="hidden" name="form_type" value={"queue_create"}/>
            <FormTextInput
                label={"キュー名"} id={"name"} placeholder={"キュー名を入力してください"} value={name}
                onChange={setName}
            />
            <FormProviderInput
                label={"プロバイダー"} id={"provider"} value={provider}
                onChange={setProvider}
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
                    to={"/task"}
                    className="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                >
                    キャンセル
                </Link>
            </div>
            <FormErrMsg err={err}/>
        </form>
    );
}
