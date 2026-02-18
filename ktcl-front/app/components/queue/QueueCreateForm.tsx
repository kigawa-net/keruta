import { Link } from "react-router";
import FormTextInput, { InputValue } from "../form/FormTextInput";
import { useEffect, useState } from "react";
import FormErrMsg from "../form/FormErrMsg";
import { useNavigate } from "react-router-dom";
import FormProviderInput from "../form/FormProviderInput";
import { useQueueService } from "../AppContext";

export function QueueCreateForm() {
  const [formState, setFormState] = useState<"inputting" | "submitting">("inputting");
  const queueService = useQueueService();
  const [provider, setProvider] = useState<InputValue>({ value: "" });
  const [name, setName] = useState<InputValue>({ value: "" });
  const [err, setErr] = useState<string>();
  const navigate = useNavigate();

  useEffect(() => {
    if (formState !== "submitting") return;
    if (name.value.trim() === "") {
      setName({ error: "キュー名を入力してください", ...name });
      setFormState("inputting");
      return;
    }
    try {
      queueService.createQueue({
        providerId: parseInt(provider.value),
        name: name.value,
      });
    } catch (e) {
      setErr(e instanceof Error ? e.message : "エラーが発生しました");
      setFormState("inputting");
    }
  }, [formState, name, provider, queueService]);

  useEffect(() => {
    return queueService.onQueueCreated((queueId) => {
      navigate(`/queue/${queueId}`);
    });
  }, [queueService, navigate]);

  return (
    <form
      className="space-y-6"
      onSubmit={(event) => {
        event.preventDefault();
        setFormState("submitting");
      }}
    >
      <input type="hidden" name="form_type" value="queue_create" />
      <FormTextInput
        label="キュー名"
        id="name"
        placeholder="キュー名を入力してください"
        value={name}
        onChange={setName}
      />
      <FormProviderInput label="プロバイダー" id="provider" value={provider} onChange={setProvider} />
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
      <FormErrMsg err={err} />
    </form>
  );
}
