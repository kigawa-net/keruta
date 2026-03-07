import {Link} from "react-router";
import FormTextInput, {InputValue} from "../form/FormTextInput";
import {useEffect, useState} from "react";
import FormErrMsg from "../form/FormErrMsg";
import {useNavigate} from "react-router-dom";
import FormProviderInput from "../form/FormProviderInput";
import {useAuthedKtseState} from "../api/AuthedKtseProvider";
import {useStateFlow} from "../../util/StateFlow";
import {useKtclApiState} from "../api/KtclApiProvider";
import {ObjectPropertyJsonField} from "../api/WellKnownTypes";
import {ClientProviderListMsg} from "../msg/provider";

type Provider = ClientProviderListMsg["providers"][0]

export function QueueCreateForm() {
    const [formState, setFormState] = useState<"inputting" | "submitting">("inputting");
    const [provider, setProvider] = useState<InputValue>({value: ""});
    const [providers, setProviders] = useState<Provider[]>([]);
    const [name, setName] = useState<InputValue>({value: ""});
    const [queueProperties, setQueueProperties] = useState<ObjectPropertyJsonField[]>([]);
    const [settingValues, setSettingValues] = useState<Record<string, string>>({});
    const [err, setErr] = useState<string>();
    const navigate = useNavigate();
    const authedKtse = useAuthedKtseState();
    const ktcl = useKtclApiState();

    useStateFlow(
        authedKtse.state === "loaded" ? authedKtse.authedKtseApi.providerListed : undefined,
        msg => setProviders(msg.providers),
    );

    // プロバイダー選択変更時に well-known を取得
    useEffect(() => {
        if (ktcl.state !== "loaded") return;
        if (!provider.value) {
            setQueueProperties([]);
            setSettingValues({});
            return;
        }
        const selectedProvider = providers.find(p => String(p.id) === provider.value);
        if (!selectedProvider) return;
        ktcl.ktclApi.getWellKnown(selectedProvider.issuer)
            .then(json => {
                setQueueProperties(json.queueProperties.fields);
                setSettingValues({});
            })
            .catch(() => {
                setQueueProperties([]);
                setSettingValues({});
            });
    }, [provider.value, providers, ktcl]);

    useEffect(() => {
        if (authedKtse.state !== "loaded") return;
        if (formState !== "submitting") return;
        if (name.value.trim() === "") {
            setName({error: "キュー名を入力してください", ...name});
            setFormState("inputting");
            return;
        }
        try {
            const setting = JSON.stringify(settingValues);
            authedKtse.authedKtseApi.createQueue(parseInt(provider.value), name.value, setting);
        } catch (e) {
            setErr(e instanceof Error ? e.message : "エラーが発生しました");
            setFormState("inputting");
        }
    }, [formState, name, provider, settingValues]);

    useStateFlow(
        authedKtse.state == "loaded" ? authedKtse.authedKtseApi.queueCreated : undefined,
        msg => {
            navigate(`/queue/${msg.queueId}`);
        },
        [navigate]
    )
    return (
        <form
            className="space-y-6"
            onSubmit={(event) => {
                event.preventDefault();
                setFormState("submitting");
            }}
        >
            <input type="hidden" name="form_type" value="queue_create"/>
            <FormTextInput
                label="キュー名"
                id="name"
                placeholder="キュー名を入力してください"
                value={name}
                onChange={setName}
            />
            <FormProviderInput label="プロバイダー" id="provider" value={provider} onChange={setProvider}/>
            {queueProperties.map(field => (
                <QueuePropertyField
                    key={field.fieldId}
                    field={field}
                    value={settingValues[field.fieldId] ?? ""}
                    onChange={v => setSettingValues(prev => ({...prev, [field.fieldId]: v}))}
                />
            ))}
            <div className="flex gap-4">
                <button
                    type="submit"
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    disabled={formState === "submitting"}
                >
                    作成
                </button>
                <Link
                    to="/task"
                    className="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                >
                    キャンセル
                </Link>
            </div>
            <FormErrMsg err={err}/>
        </form>
    );
}

function QueuePropertyField(
    {field, value, onChange}: {
        field: ObjectPropertyJsonField,
        value: string,
        onChange: (v: string) => void,
    }
) {
    const inputType = field.value.type === "NumberPropertyJson" ? "number" : "text";
    return (
        <div>
            <label htmlFor={field.fieldId} className="block text-sm font-medium text-gray-700 mb-2">
                {field.fieldName}
            </label>
            <input
                type={inputType}
                id={field.fieldId}
                value={value}
                onChange={e => onChange(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder={field.fieldName}
            />
        </div>
    );
}