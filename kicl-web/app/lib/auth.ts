import { UserManager, WebStorageStateStore, type UserManagerSettings } from 'oidc-client-ts';

export function createUserManager(issuerUrl: string, clientId: string): UserManager {
    const settings: UserManagerSettings = {
        authority: issuerUrl,
        client_id: clientId,
        redirect_uri: `${window.location.origin}/auth/callback`,
        scope: 'openid profile email',
        userStore: new WebStorageStateStore({ store: window.localStorage }),
        automaticSilentRenew: false,
    };
    return new UserManager(settings);
}
