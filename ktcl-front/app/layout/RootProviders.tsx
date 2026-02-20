import { ReactNode, useState, useEffect } from "react";
import { KeycloakProvider } from "../components/auth/Keycloak";
import { UserProfileProvider } from "../components/user/UserProfile";
import { ServiceProvider } from "../components/service/ServiceContext";
import { AppProvider } from "../components/app/AppContext";
import Config from "../Config";

interface RootProvidersProps {
  children: ReactNode;
}

/**
 * アプリケーション全体のProviderツリー
 * Keycloak → UserProfile → Service → App の順でネスト
 * SSR対応: クライアントサイドでのみWebSocket接続を確立
 */
export function RootProviders({ children }: RootProvidersProps) {
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
        <ServiceProvider wsUrl={Config.websocketUrl}>
          <AppProvider>{children}</AppProvider>
        </ServiceProvider>
      </UserProfileProvider>
    </KeycloakProvider>
  );
}
