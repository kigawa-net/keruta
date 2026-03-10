export interface ProviderPreset {
    name: string;
    issuer: string;
    description: string;
}

function loadPresets(): ProviderPreset[] {
    const raw = import.meta.env.VITE_PROVIDER_PRESETS;
    if (!raw) return [];
    try {
        return JSON.parse(raw) as ProviderPreset[];
    } catch {
        console.error("VITE_PROVIDER_PRESETS のパースに失敗しました:", raw);
        return [];
    }
}

export const PROVIDER_PRESETS: ProviderPreset[] = loadPresets();
