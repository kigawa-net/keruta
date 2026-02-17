import { useContext } from "react";
import { AppContext } from "../AppContext";
import type { AppState, KerutaTaskState } from "../AppContext";

/**
 * AppContext から状態を取得するベースフック
 */
export function useAppState(): AppState {
  const context = useContext(AppContext);
  if (!context) throw new Error("useAppState must be used within AppProvider");
  return context;
}

/**
 * KerutaTaskState を取得するフック
 */
export function useKerutaTaskState(): KerutaTaskState {
  return useAppState().kerutaState;
}
