import {InputValue} from "../form/FormTextInput";

interface ValidationResult {
    valid: boolean;
    error?: string;
}

function validateIssuer(issuer: string): ValidationResult {
    if (issuer.trim() === "") {
        return {valid: false, error: "Issuerを入力してください"};
    }
    try {
        new URL(issuer);
    } catch {
        return {valid: false, error: "有効なURLを入力してください"};
    }
    return {valid: true};
}

interface ProviderFormValidation {
    issuer: InputValue;
    setIssuer: (value: InputValue) => void;
}

export function validateProviderForm(
    {issuer, setIssuer}: ProviderFormValidation,
): boolean {
    const issuerValidation = validateIssuer(issuer.value);
    if (!issuerValidation.valid) {
        setIssuer({...issuer, error: issuerValidation.error});
        return false;
    }

    return true;
}
