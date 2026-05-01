import {Link} from "react-router";
import FormTextInput, {InputValue} from "../form/FormTextInput";
import {useEffect, useState} from "react";
import FormErrMsg from "../form/FormErrMsg";
import {useNavigate} from "react-router-dom";
import {useAuthedKtseState} from "../api/AuthedKtseProvider";
import {useStateFlow} from "../../util/StateFlow";

export function QueueEditForm({queueId}: { queueId: number }) {
    const [formState, setFormState] = useState<"loading" | "inputting" | "submitting">("loading");
    const [name, setName] = useState<InputValue>({value: ""});
    const [err, setErr] = useState<string>();
    const navigate = useNavigate();
    const authedKtse = useAuthedKtseState();

    useEffect(() => {
        if (authedKtse.state !== "loaded") return;
        authedKtse.authedKtseApi.showQueue(queueId);
    }, [authedKtse, queueId]);

    useStateFlow(
        authedKtse.state === "loaded" ? authedKtse.authedKtseApi.queueShowed : undefined,
        msg => {
            if (msg.id !== queueId) return;
            setName({value: msg.name});
            setFormState("inputting");
        },
        [queueId]
    );

    useEffect(() => {
        if (authedKtse.state !== "loaded") return;
        if (formState !== "submitting") return;
        if (name.value.trim() === "") {
            setName({error: "キュー名を入力してください", ...name});
            setFormState("inputting");
            return;
        }
        try {
            authedKtse.authedKtseApi.updateQueue(queueId, name.value);
        } catch (e) {
            setErr(e instanceof Error ? e.message : "エラーが発生しました");
            setFormState("inputting");
        }
    }, [formState, name, queueId, authedKtse]);

    useStateFlow(
        authedKtse.state === "loaded" ? authedKtse.authedKtseApi.queueUpdated : undefined,
        msg => {
            if (msg.id !== queueId) return;
            navigate(`/queue/create`);
        },
        [navigate, queueId]
    );

    if (formState === "loading") {
        return <div className="text-gray-500">読み込み中...</div>;
    }

    return (
        <form
            className="space-y-6"
            onSubmit={(event) => {
                event.preventDefault();
                setFormState("submitting");
            }}
        >
            <FormTextInput
                label="キュー名"
                id="name"
                placeholder="キュー名を入力してください"
                value={name}
                onChange={setName}
            />
            <div className="flex gap-4">
                <button
                    type="submit"
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    disabled={formState === "submitting"}
                >
                    更新
                </button>
                <Link
                    to="/queue/create"
                    className="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
                >
                    キャンセル
                </Link>
            </div>
            <FormErrMsg err={err}/>
        </form>
    );
}
