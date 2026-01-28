import {Link} from "react-router";
import FormTextInput, {TextInputValue} from "../form/FormTextInput";
import {useEffect, useState} from "react";
import {TaskCreateMsg} from "../../msg/task";
import {useWsState} from "../websocket/Websocket";
import FormErrMsg from "../form/FormErrMsg";


export function TaskCreateForm() {
    const [formState, setFormState] = useState<"inputting" | "submitting">("inputting")
    const ws = useWsState()
    const [name, setName] = useState<TextInputValue>({value: ""})
    const [err, setErr] = useState<string>()

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
        const msg: TaskCreateMsg = {
            type: "task_create",
            name: name.value,
            queueId: "todo" // todo
        }
        ws.websocket.send(JSON.stringify(msg))

    }, [formState])
    return (
        <form className="space-y-6" onSubmit={(event) => {
            event.preventDefault()
            setFormState("submitting")
        }}>
            <input type="hidden" name="form_type" value={"task_create"}/>
            <FormTextInput
                label={"タスク名"} id={"taskName"} placeholder={"タスク名を入力してください"} value={name}
                onChange={setName}
            />

            <div className="flex gap-4">
                <button
                    type="submit"
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
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
