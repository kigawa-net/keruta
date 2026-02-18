export type KerutaTaskState =
  | { state: "unloaded" }
  | ConnectedKerutaTaskState
  | { state: "disconnected" };

export interface ConnectedKerutaTaskState {
  state: "connected";
  auth: AuthState;
}

export type AuthState = { state: "unauthenticated" } | { state: "authenticated" };
