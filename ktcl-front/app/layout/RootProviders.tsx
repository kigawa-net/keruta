import {ReactNode, useEffect, useState} from "react";
import {KeycloakProvider} from "../components/auth/Keycloak";
import {UserProfileProvider} from "../components/user/UserProfile";
import {ServiceProvider} from "../components/service/ServiceContext";

import Config from "../Config";
import {GlobalProvider} from "../components/app/Global";
import {AppContentProvider} from "../components/app/AppContentContext";

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
        <GlobalProvider wsUrl={Config.websocketUrl} >
            <KeycloakProvider>
                <UserProfileProvider>
                    <ServiceProvider wsUrl={Config.websocketUrl}>
                        <AppContentProvider>{children}</AppContentProvider>
                    </ServiceProvider>
                </UserProfileProvider>
            </KeycloakProvider>
        </GlobalProvider>
    );
}
