import Keycloak from 'keycloak-js'
import Config from "../../Config";

// Keycloak設定
export const keycloakConfig: Keycloak.KeycloakConfig = {
    url: Config.keycloakUrl,
    realm: Config.keycloakRealm,
    clientId: Config.keycloakClientId,
}

// Keycloakインスタンスの初期化
export const keycloakClient = new Keycloak(keycloakConfig)

// 3rd-party cookieチェックを無効化（ブラウザのCookie制限対応）
// これにより iframe ベースの3rd-party cookieチェックがスキップされ、
// "Timeout when waiting for 3rd party check iframe message" エラーが解消されます
keycloakClient.init({
    checkLoginIframe: Config.keycloakCheckLoginIframe,
})
