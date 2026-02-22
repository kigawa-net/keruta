import { useContext } from "react";
import {KerutaTaskState} from "../../util/net/websocket/ConnectionStateTypes";
import {AppContentContext, AppState} from "./AppContentContext";

const defaultKerutaState: KerutaTaskState = { state: "unloaded" }

/**
 * AppContext から状態を取得するベースフック
 */
export function useAppState(): AppState {
  const context = useContext(AppContentContext);
  if (!context) {
    return {
      kerutaState: defaultKerutaState,
      taskService: null as never,
      queueService: null as never,
      providerService: null as never,
    }
  }
  return context;
}

/**
 * KerutaTaskState を取得するフック
 */
export function useKerutaTaskState(): KerutaTaskState {
  return useAppState().kerutaState;
}
