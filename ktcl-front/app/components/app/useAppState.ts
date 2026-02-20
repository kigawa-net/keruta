import { useContext } from "react";
import { AppContext } from "./AppContext";
import type { AppState, KerutaTaskState } from "./AppContext";

const defaultKerutaState: KerutaTaskState = { state: "unloaded" }

/**
 * AppContext から状態を取得するベースフック
 */
export function useAppState(): AppState {
  const context = useContext(AppContext);
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
