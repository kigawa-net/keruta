import FormErrMsg from "./FormErrMsg";
import {InputValue} from "./FormTextInput";
import {useEffect, useState} from "react";
import {ClientProviderListMsg, ServerProviderListMsg} from "../msg/provider";
import useWsReceive from "../net/websocket/useWsReceive";


import {useKerutaTaskState} from "../app/useAppState";
import {useGlobalState} from "../app/Global";


type Provider = ClientProviderListMsg["providers"][0]
export default function FormProviderInput(
    {
        label,
        id,
        value,
        onChange,
    }: {
        label: string,
        id: string,
        value: InputValue,
        onChange: (value: InputValue) => void,
    },
) {
    const [providers, setProviders] = useState<Provider[]>()
    const globalState = useGlobalState()
    const keruta = useKerutaTaskState()
    useWsReceive(globalState, msg => {
        if (msg.type != "provider_listed") return
        setProviders(msg.providers)
    }, [])
    useEffect(() => {
        if (globalState.state != "open") return
        if (keruta.state != "connected") return;
        if (keruta.auth.state != "authenticated") return;
        const res: ServerProviderListMsg = {
            type: "provider_list",
        }
        globalState.websocket.send(JSON.stringify(res))
    }, [globalState.state, keruta.state,keruta.state == "connected" && keruta.auth.state]);
    return (
        <div>
            <label htmlFor={id} className="block text-sm font-medium text-gray-700 mb-2">
                {label}
            </label>
            <select
                id={id}
                value={value.value}
                onChange={
                    (e) => onChange({value: e.target.value, error: undefined})
                }
                className={
                    "w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2" +
                    " focus:ring-blue-500"
                }
            >
                <option value="">--- 選択 ---</option>
                {providers?.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
            </select>
            <FormErrMsg err={value.error}/>
        </div>
    )
}


