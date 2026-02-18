import { useEffect } from "react";
import { useWsState, useAuthMessageService, useTokenApiService } from "../../service/useServiceHooks";
import { useKerutaTaskState } from "../../app/AppContext";
import { KeycloakState, useKeycloakState } from "../../auth/Keycloak";
import type { WsState } from "./useWebSocketConnection";
import type { KerutaTaskState } from "../../app/AppContext";

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
      if (wsState.state !== "open") return;
      try {
        const userToken = await kc.getToken();
        const result = await tokenApiService.getServerToken(userToken);
        // Check again after async operations - WebSocket may have closed during await
        if (wsState.state !== "open") return;
        if (result.success) {
          authMsgService.sendAuthRequest(userToken, result.data.token);
        }
      } catch (e) {
        console.warn("Auth request failed:", e);
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
