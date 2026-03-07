import React from "react";
import FormTextInput from "../form/FormTextInput";
import FormErrMsg from "../form/FormErrMsg";
import {ProviderAddFormActions} from "./ProviderAddFormActions";
import {useProviderAddForm} from "./useProviderAddForm";

export function ProviderAddForm() {
    const {issuer, err, isDisabled, buttonLabel, setIssuer, handleSubmit} = useProviderAddForm();

    return (
        <form
            className="space-y-6"
            onSubmit={(e) => {
                e.preventDefault();
                void handleSubmit();
            }}
        >
            <FormTextInput
                label="Issuer" id="issuer" placeholder="https://provider.example.com" value={issuer}
                onChange={setIssuer}
            />
            <ProviderAddFormActions isDisabled={isDisabled} buttonLabel={buttonLabel}/>
            <FormErrMsg err={err}/>
        </form>
    );
}
