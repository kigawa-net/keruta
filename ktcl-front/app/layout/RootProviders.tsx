import {ReactNode, useEffect, useState} from "react";
import {KeycloakProvider} from "../components/auth/Keycloak";
import {UserProfileProvider} from "../components/user/UserProfile";
import {ServiceProvider} from "../util/net/service/ServiceContext";

import Config from "../Config";
import {AppContentProvider} from "../components/app/AppContentContext";
import {KtclApiProvider} from "../components/api/KtclApiProvider";
import {WebsocketProvider} from "../util/net/websocket/WebsocketProvider";
import {KtseApiProvider} from "../components/api/KtseApiProvider";
import {AuthedKtseProvider} from "../components/api/AuthedKtseProvider";

interface RootProvidersProps {
    children: ReactNode;
}

/**
 * アプリケーション全体のProviderツリー
 * Keycloak → UserProfile → Service → App の順でネスト
 * SSR対応: クライアントサイドでのみWebSocket接続を確立
 */
export function RootProviders({children}: RootProvidersProps) {
    const [isClient, setIsClient] = useState(false);

    useEffect(() => {
        setIsClient(true);
    }, []);

    if (!isClient) {
        return <>{children}</>;
    }

    return (
        <KeycloakProvider>
            <UserProfileProvider>
                <KtclApiProvider>
                    <WebsocketProvider wsUrl={Config.websocketUrl}>
                        <KtseApiProvider>
                            <AuthedKtseProvider>
                                <ServiceProvider wsUrl={Config.websocketUrl}>
                                    <AppContentProvider>{children}</AppContentProvider>
                                </ServiceProvider>
                            </AuthedKtseProvider>
                        </KtseApiProvider>
                    </WebsocketProvider>
                </KtclApiProvider>
            </UserProfileProvider>
        </KeycloakProvider>
    );
}
