import Keycloak from 'keycloak-js'
import { Config } from './Config'

// Keycloak設定
export const keycloakConfig: Keycloak.KeycloakConfig = {
    url: Config.keycloak.url,
    realm: Config.keycloak.realm,
    clientId: Config.keycloak.clientId,
}

// Keycloakインスタンスの初期化
export const keycloak = new Keycloak(keycloakConfig)