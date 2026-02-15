import {InputValue} from "../form/FormTextInput";

export interface ValidationResult {
    valid: boolean;
    error?: string;
}

export function validateName(name: string): ValidationResult {
    if (name.trim() === "") {
        return {valid: false, error: "名前を入力してください"};
    }
    return {valid: true};
}

export function validateIssuer(issuer: string): ValidationResult {
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

export interface ProviderFormValidation {
    name: InputValue;
    setName: (value: InputValue) => void;
    issuer: InputValue;
    setIssuer: (value: InputValue) => void;
}

export function validateProviderForm(
    {name, setName, issuer, setIssuer}: ProviderFormValidation,
): boolean {
    const nameValidation = validateName(name.value);
    if (!nameValidation.valid) {
        setName({...name, error: nameValidation.error});
        return false;
    }

    const issuerValidation = validateIssuer(issuer.value);
    if (!issuerValidation.valid) {
        setIssuer({...issuer, error: issuerValidation.error});
        return false;
    }

    return true;
}
