import {ClientProviderAddTokenMsg} from "../../msg/provider";

interface ProviderAuthUrlParams {
    login: string;
    token: string;
}

export function buildProviderAuthUrl(
    {login, token}: ProviderAuthUrlParams,
): URL {
    const url = new URL(login);
    url.searchParams.set("token", token);
    return url;
}

export function buildProviderAuthUrlFromMsg(
    login: string,
    msg: ClientProviderAddTokenMsg,
): URL {
    return buildProviderAuthUrl({
        login,
        token: msg.token,
    });
}
