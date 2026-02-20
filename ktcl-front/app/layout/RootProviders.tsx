import { ReactNode } from "react";
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
 */
export function RootProviders({ children }: RootProvidersProps) {
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
