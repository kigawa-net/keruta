import { useEffect } from "react";
import { useWsState, useAuthMessageService, useTokenApiService } from "../useServiceHooks";
import { KerutaTaskState, useKerutaTaskState } from "../KerutaTask";
import { KeycloakState, useKeycloakState } from "../Keycloak";
import type { WsState } from "../useWebSocketConnection";

export default function WsSender() {
  const wsState = useWsState();
  const kc = useKeycloakState();
  const kerutaState = useKerutaTaskState();
  useAuth(wsState, kc, kerutaState);
  useRetry(wsState);
  return null;
}

function useAuth(wsState: WsState, kc: KeycloakState, kerutaState: KerutaTaskState) {
  const authMsgService = useAuthMessageService();
  const tokenApiService = useTokenApiService();

  useEffect(() => {
    const shouldAuth =
      wsState.state === "open" &&
      kc.state === "authenticated" &&
      kerutaState.state === "connected" &&
      kerutaState.auth.state === "unauthenticated";
    if (!shouldAuth) return;

    const doAuth = async () => {
      const userToken = await kc.getToken();
      const result = await tokenApiService.getServerToken(userToken);
      if (result.success) {
        authMsgService.sendAuthRequest(userToken, result.data.token);
      }
    };

    const interval = setInterval(doAuth, 5000);
    doAuth();
    return () => clearInterval(interval);
  }, [wsState.state, kc, kerutaState, authMsgService, tokenApiService]);
}

function useRetry(wsState: WsState) {
  useEffect(() => {
    if (wsState.state === "closed") {
      const timeout = setTimeout(() => wsState.open(), 5000);
      return () => clearTimeout(timeout);
    }
    if (wsState.state === "error") {
      const timeout = setTimeout(() => wsState.retry(), 10000);
      return () => clearTimeout(timeout);
    }
  }, [wsState]);
}
