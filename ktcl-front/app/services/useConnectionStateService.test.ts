import { describe, it, expect } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useConnectionStateService } from '../components/net/websocket/useConnectionStateService';
import type { WsState } from '../components/net/websocket/WsTypes';

describe('useConnectionStateService', () => {
  describe('初期状態', () => {
    it('wsStateがunloadedの場合、kerutaStateはunloaded', () => {
      const wsState: WsState = { state: 'unloaded' };
      const { result } = renderHook(() => useConnectionStateService(wsState));

      expect(result.current.kerutaState).toEqual({ state: 'unloaded' });
    });

    it('wsStateがloadedの場合、kerutaStateはunloaded', () => {
      const wsState: WsState = { state: 'loaded', websocket: {} as WebSocket };
      const { result } = renderHook(() => useConnectionStateService(wsState));

      expect(result.current.kerutaState).toEqual({ state: 'unloaded' });
    });
  });

  describe('WebSocket接続状態の変化', () => {
    it('wsStateがopenになると、kerutaStateはconnected + unauthenticated', () => {
      const { result, rerender } = renderHook(
        ({ wsState }) => useConnectionStateService(wsState),
        { initialProps: { wsState: { state: 'unloaded' } as WsState } }
      );

      expect(result.current.kerutaState).toEqual({ state: 'unloaded' });

      // WebSocketがopenになる
      const mockWs = {} as WebSocket;
      rerender({
        wsState: {
          state: 'open',
          websocket: mockWs,
          close: () => {},
        } as WsState,
      });

      expect(result.current.kerutaState).toEqual({
        state: 'connected',
        auth: { state: 'unauthenticated' },
      });
    });

    it('wsStateがclosedになると、kerutaStateはdisconnected', () => {
      const mockWs = {} as WebSocket;
      const { result, rerender } = renderHook(
        ({ wsState }) => useConnectionStateService(wsState),
        {
          initialProps: {
            wsState: {
              state: 'open',
              websocket: mockWs,
              close: () => {},
            } as WsState,
          },
        }
      );

      expect(result.current.kerutaState).toEqual({
        state: 'connected',
        auth: { state: 'unauthenticated' },
      });

      // WebSocketが閉じる
      rerender({
        wsState: {
          state: 'closed',
          websocket: mockWs,
          open: () => {},
        } as WsState,
      });

      expect(result.current.kerutaState).toEqual({ state: 'disconnected' });
    });
  });

  describe('認証状態の更新', () => {
    it('setAuthStateで認証状態をauthenticatedに更新できる', () => {
      const mockWs = {} as WebSocket;
      const { result } = renderHook(() =>
        useConnectionStateService({
          state: 'open',
          websocket: mockWs,
          close: () => {},
        } as WsState)
      );

      expect(result.current.kerutaState).toEqual({
        state: 'connected',
        auth: { state: 'unauthenticated' },
      });

      act(() => {
        result.current.setAuthState({ state: 'authenticated' });
      });

      expect(result.current.kerutaState).toEqual({
        state: 'connected',
        auth: { state: 'authenticated' },
      });
    });

    it('WebSocketが再接続されると認証状態はunauthenticatedにリセット', () => {
      const mockWs = {} as WebSocket;
      const { result, rerender } = renderHook(
        ({ wsState }) => useConnectionStateService(wsState),
        {
          initialProps: {
            wsState: {
              state: 'open',
              websocket: mockWs,
              close: () => {},
            } as WsState,
          },
        }
      );

      // 認証済みにする
      act(() => {
        result.current.setAuthState({ state: 'authenticated' });
      });

      expect(result.current.kerutaState).toEqual({
        state: 'connected',
        auth: { state: 'authenticated' },
      });

      // WebSocketが閉じる
      rerender({
        wsState: {
          state: 'closed',
          websocket: mockWs,
          open: () => {},
        } as WsState,
      });

      expect(result.current.kerutaState).toEqual({ state: 'disconnected' });

      // WebSocketが再接続
      rerender({
        wsState: {
          state: 'open',
          websocket: mockWs,
          close: () => {},
        } as WsState,
      });

      // 認証状態はリセットされる
      expect(result.current.kerutaState).toEqual({
        state: 'connected',
        auth: { state: 'unauthenticated' },
      });
    });
  });
});
