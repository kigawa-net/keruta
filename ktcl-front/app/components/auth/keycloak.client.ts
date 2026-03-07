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
