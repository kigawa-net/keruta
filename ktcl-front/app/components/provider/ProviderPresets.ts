export interface ProviderPreset {
    name: string;
    issuer: string;
    description: string;
}

export function loadPresets(): ProviderPreset[] {
    const raw = import.meta.env.VITE_PROVIDER_PRESETS;
    console.log("VITE_PROVIDER_PRESETS:", raw);
    if (!raw) return [];
    try {
        return JSON.parse(raw) as ProviderPreset[];
    } catch {
        console.error("VITE_PROVIDER_PRESETS のパースに失敗しました:", raw);
        return [];
    }
}

