import {useCallback, useState} from "react";
import {buildProviderAuthUrlFromMsg} from "./providerAuthUrl";
import {validateProviderForm} from "./providerValidation";
import {Url} from "../../util/net/Url";
import type {InputValue} from "../form/FormTextInput";
import {useAuthedKtseState} from "../api/AuthedKtseProvider";
import {useStateFlow} from "../../util/StateFlow";

type FormState = "inputting" | "fetching" | "submitting" | "redirecting";

type KerutaJson = { login: string };

interface ProviderAddFormState {
    formState: FormState;
    issuer: InputValue;
    err: string | undefined;
    isDisabled: boolean;
    buttonLabel: string;
    setIssuer: (value: InputValue) => void;
    handleSubmit: () => Promise<void>;
}

export function useProviderAddForm(): ProviderAddFormState {
    const authedKtse = useAuthedKtseState();
    const [formState, setFormState] = useState<FormState>("inputting");
    const [issuer, setIssuer] = useState<InputValue>({value: ""});
    const [err, setErr] = useState<string>();
    const [kerutaJson, setKerutaJson] = useState<KerutaJson | undefined>(undefined);

    const handleTokenReceived = useCallback(
        (token: string) => {
            if (!kerutaJson) return;
            const url = buildProviderAuthUrlFromMsg(kerutaJson.login, token);
            setFormState("redirecting");
            window.location.href = url.toStrUrl();
        },
        [kerutaJson]
    );
    useStateFlow(
        authedKtse.state === "loaded" ? authedKtse.authedKtseApi.providerTokenIssued : undefined,
        msg => {
            handleTokenReceived(msg.token);
        },
        [handleTokenReceived]
    )
    const handleSubmit = useCallback(async () => {
        if (authedKtse.state !== "loaded") {
            setErr("認証されていません");
            return;
        }

        const isValid = validateProviderForm({issuer, setIssuer});
        if (!isValid) return;

        setFormState("fetching");
        const issuerUrl = Url.parse(issuer.value.replace(/\/$/, ""));
        let json: KerutaJson;
        try {
            const res = await fetch(issuerUrl.plusPath(".well-known/keruta.json").toStrUrl());
            json = await res.json();
        } catch {
            setErr("keruta.jsonの取得に失敗しました");
            setFormState("inputting");
            return;
        }

        setKerutaJson(json);
        setFormState("submitting");
        authedKtse.authedKtseApi.providerIssuerToken(issuerUrl.toStrUrl());
    }, [authedKtse, issuer]);

    const isDisabled = formState !== "inputting";
    const buttonLabel =
        formState === "redirecting"
            ? "リダイレクト中..."
            : formState === "fetching" || formState === "submitting"
                ? "処理中..."
                : "追加";

    return {
        formState,
        issuer,
        err,
        isDisabled,
        buttonLabel,
        setIssuer,
        handleSubmit,
    };
}
