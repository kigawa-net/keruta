import {Url} from "../../util/net/Url";

interface ProviderAuthUrlParams {
    login: string;
    token: string;
}

function buildProviderAuthUrl({login, token}: ProviderAuthUrlParams): Url {
    return Url.parse(login).setQueryParam("token", token);
}

export function buildProviderAuthUrlFromMsg(login: string, token: string): Url {
    return buildProviderAuthUrl({
        login,
        token: token,
    });
}
