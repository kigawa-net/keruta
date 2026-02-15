import {ClientProviderAddTokenMsg} from "../../msg/provider";

interface ProviderAuthUrlParams {
    authorizationEndpoint: string;
    audience: string;
    token: string;
}

export function buildProviderAuthUrl(
    {authorizationEndpoint, audience, token}: ProviderAuthUrlParams,
): URL {
    const url = new URL(authorizationEndpoint);
    url.searchParams.set("state", token);
    url.searchParams.set("redirect_uri", `${window.location.origin}/provider/complete`);
    url.searchParams.set("client_id", audience);
    url.searchParams.set("response_type", "code");
    url.searchParams.set("scope", "openid");
    return url;
}

export function buildProviderAuthUrlFromMsg(
    authorizationEndpoint: string,
    audience: string,
    msg: ClientProviderAddTokenMsg,
): URL {
    return buildProviderAuthUrl({
        authorizationEndpoint,
        audience,
        token: msg.token,
    });
}
