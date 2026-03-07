export function buildOidcLoginUrl(authorizationEndpoint: string, clientId: string): string {
    const url = new URL(authorizationEndpoint)
    url.searchParams.set("response_type", "code")
    url.searchParams.set("client_id", clientId)
    url.searchParams.set("redirect_uri", window.location.origin)
    url.searchParams.set("scope", "openid")
    return url.toString()
}