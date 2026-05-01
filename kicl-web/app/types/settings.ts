export type Language = 'ja' | 'en';

export interface AppSettings {
    ownIssuerUrl: string;
    userIssuerUrl: string;
    ktseUrl: string;
    language: Language;
}

export const defaultSettings: AppSettings = {
    ownIssuerUrl: '',
    userIssuerUrl: '',
    ktseUrl: '',
    language: 'ja',
};

export const SETTINGS_STORAGE_KEY = 'kicl-settings';
