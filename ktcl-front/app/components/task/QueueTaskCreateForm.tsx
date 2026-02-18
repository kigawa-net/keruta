import { useEffect, useState } from "react";
import { useWsState } from "../service/useServiceHooks";
import FormTextInput, { InputValue } from "../form/FormTextInput";
import FormErrMsg from "../form/FormErrMsg";
import { ServerTaskCreateMsg } from "../msg/task";

interface QueueTaskCreateFormProps {
    queueId: string;
    onTaskCreated?: () => void;
}

export function QueueTaskCreateForm({queueId, onTaskCreated}: QueueTaskCreateFormProps) {
    const wsState = useWsState();
    const [formState, setFormState] = useState<"inputting" | "submitting">("inputting");
    const [taskName, setTaskName] = useState<InputValue>({value: ""});
    const [description, setDescription] = useState<InputValue>({value: ""});
    const [err, setErr] = useState<string>();

    useEffect(() => {
        if (formState !== "submitting") return;
        if (wsState.state !== "open") {
            setErr("websocket is not connected");
            setFormState("inputting");
            return;
        }
        if (taskName.value.trim() === "") {
            setTaskName({error: "タスク名を入力してください", ...taskName});
            setFormState("inputting");
            return;
        }

        const msg: ServerTaskCreateMsg = {
            type: "task_create",
            queueId: parseInt(queueId),
            title: taskName.value,
            description: description.value.trim(),
        };
        wsState.websocket.send(JSON.stringify(msg));

        setTaskName({value: ""});
        setDescription({value: ""});
        setFormState("inputting");
        onTaskCreated?.();
    }, [formState]);

    return (
        <div className="bg-white rounded-lg shadow p-6 mb-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">新しいタスクを作成</h2>
            <form className="space-y-4" onSubmit={(event) => {
                event.preventDefault();
                setFormState("submitting");
            }}>
                <FormTextInput
                    label="タスク名"
                    id="taskName"
                    placeholder="タスク名を入力してください"
                    value={taskName}
                    onChange={setTaskName}
                />
                <FormTextInput
                    label="タスクの説明"
                    id="description"
                    placeholder="タスクの説明を入力してください"
                    value={description}
                    onChange={setDescription}
                />
                <div className="flex gap-4">
                    <button
                        type="submit"
                        className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400"
                        disabled={formState === "submitting"}
                    >
                        {formState === "submitting" ? "作成中..." : "タスクを作成"}
                    </button>
                </div>
                <FormErrMsg err={err}/>
            </form>
        </div>
    );
}
