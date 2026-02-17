import React, { useCallback, useEffect, useState } from "react";
import FormTextInput, { InputValue } from "../form/FormTextInput";
import FormErrMsg from "../form/FormErrMsg";
import { useKerutaTaskState } from "../KerutaTask";
import { ProviderAddFormActions } from "./ProviderAddFormActions";
import { buildProviderAuthUrlFromMsg } from "./providerAuthUrl";
import { validateProviderForm } from "./providerValidation";
import { useProviderService } from "../DomainContext";

type FormState = "inputting" | "fetching" | "submitting" | "redirecting";

type KerutaJson = { login: string };

export function ProviderAddForm() {
  const kerutaState = useKerutaTaskState();
  const providerService = useProviderService();
  const [formState, setFormState] = useState<FormState>("inputting");
  const [name, setName] = useState<InputValue>({ value: "" });
  const [issuer, setIssuer] = useState<InputValue>({ value: "" });
  const [err, setErr] = useState<string>();
  const [kerutaJson, setKerutaJson] = useState<KerutaJson | undefined>(undefined);

  const handleTokenReceived = useCallback(
    (token: string) => {
      if (!kerutaJson) return;
      const url = buildProviderAuthUrlFromMsg(kerutaJson.login, { type: "provider_add_token_issued", token });
      setFormState("redirecting");
      window.location.href = url.toString();
    },
    [kerutaJson]
  );

  useEffect(() => {
    return providerService.onTokenIssued(handleTokenReceived);
  }, [providerService, handleTokenReceived]);

  const handleSubmit = async () => {
    if (kerutaState.state !== "connected" || kerutaState.auth.state !== "authenticated") {
      setErr("認証されていません");
      return;
    }

    const isValid = validateProviderForm({ name, setName, issuer, setIssuer });
    if (!isValid) return;

    setFormState("fetching");
    const issuerValue = issuer.value.replace(/\/$/, "");
    let json: KerutaJson;
    try {
      const res = await fetch(`${issuerValue}/.well-known/keruta.json`);
      json = await res.json();
    } catch {
      setErr("keruta.jsonの取得に失敗しました");
      setFormState("inputting");
      return;
    }

    setKerutaJson(json);
    setFormState("submitting");
    providerService.addProvider({ name: name.value, issuer: issuerValue, audience: "keruta" });
  };

  const isDisabled = formState !== "inputting";
  const buttonLabel =
    formState === "redirecting"
      ? "リダイレクト中..."
      : formState === "fetching" || formState === "submitting"
        ? "処理中..."
        : "追加";

  return (
    <form className="space-y-6" onSubmit={(e) => { e.preventDefault(); void handleSubmit(); }}>
      <FormTextInput label="名前" id="name" placeholder="プロバイダーの名前" value={name} onChange={setName} />
      <FormTextInput label="Issuer" id="issuer" placeholder="https://provider.example.com" value={issuer} onChange={setIssuer} />
      <ProviderAddFormActions isDisabled={isDisabled} buttonLabel={buttonLabel} />
      <FormErrMsg err={err} />
    </form>
  );
}
