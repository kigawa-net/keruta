import {ReactNode, useEffect, useState} from "react";
import {KiseAuthProvider} from "../components/auth/KiseAuth";
import {UserProfileProvider} from "../components/user/UserProfile";

import Config from "../Config";
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
        <KiseAuthProvider>
            <UserProfileProvider>
                <KtclApiProvider>
                    <WebsocketProvider wsUrl={Config.websocketUrl}>
                        <KtseApiProvider>
                            <AuthedKtseProvider>
                                {children}
                            </AuthedKtseProvider>
                        </KtseApiProvider>
                    </WebsocketProvider>
                </KtclApiProvider>
            </UserProfileProvider>
        </KiseAuthProvider>
    );
}
