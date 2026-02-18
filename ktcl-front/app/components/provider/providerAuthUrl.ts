import { Url } from "../net/Url";
import { ClientProviderAddTokenMsg } from "../msg/provider";

interface ProviderAuthUrlParams {
  login: string;
  token: string;
}

export function buildProviderAuthUrl({ login, token }: ProviderAuthUrlParams): Url {
  return Url.parse(login).setQueryParam("token", token);
}

export function buildProviderAuthUrlFromMsg(login: string, msg: ClientProviderAddTokenMsg): Url {
  return buildProviderAuthUrl({
    login,
    token: msg.token,
  });
}
