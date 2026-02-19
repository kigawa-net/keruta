import { describe, it, expect, vi } from 'vitest';
import { createMessageHandler } from '../components/msg/MessageRouterService';
import type { TaskService } from '../components/domain';
import type { QueueService } from '../components/domain';
import type { ProviderService } from '../components/domain';

describe('createMessageHandler', () => {
  const createMockServices = () => ({
    taskService: {
      handleMessage: vi.fn(),
    } as unknown as TaskService,
    queueService: {
      handleMessage: vi.fn(),
    } as unknown as QueueService,
    providerService: {
      handleMessage: vi.fn(),
    } as unknown as ProviderService,
  });

  const createMockWebSocket = () => ({} as WebSocket);

  describe('auth_successメッセージの処理', () => {
    it('auth_successメッセージを受信したらonAuthSuccessが呼ばれる', () => {
      const services = createMockServices();
      const onAuthSuccess = vi.fn();
      const ws = createMockWebSocket();

      const handler = createMessageHandler({
        ws,
        ...services,
        onAuthSuccess,
      });

      const event = {
        data: JSON.stringify({ type: 'auth_success' }),
      } as MessageEvent;

      handler(event);

      expect(onAuthSuccess).toHaveBeenCalledTimes(1);
      expect(services.taskService.handleMessage).not.toHaveBeenCalled();
      expect(services.queueService.handleMessage).not.toHaveBeenCalled();
      expect(services.providerService.handleMessage).not.toHaveBeenCalled();
    });
  });

  describe('タスクメッセージの処理', () => {
    it('task_listedメッセージを受信したらtaskService.handleMessageが呼ばれる', () => {
      const services = createMockServices();
      const onAuthSuccess = vi.fn();
      const ws = createMockWebSocket();

      const handler = createMessageHandler({
        ws,
        ...services,
        onAuthSuccess,
      });

      const event = {
        data: JSON.stringify({
          type: 'task_listed',
          tasks: [
            { id: 1, title: 'Task 1', description: 'Description', status: 'pending' },
          ],
        }),
      } as MessageEvent;

      handler(event);

      expect(services.taskService.handleMessage).toHaveBeenCalledTimes(1);
      expect(services.queueService.handleMessage).toHaveBeenCalledTimes(1);
      expect(services.providerService.handleMessage).toHaveBeenCalledTimes(1);
      expect(onAuthSuccess).not.toHaveBeenCalled();
    });
  });

  describe('キューメッセージの処理', () => {
    it('queue_listedメッセージを受信したら各サービスのhandleMessageが呼ばれる', () => {
      const services = createMockServices();
      const onAuthSuccess = vi.fn();
      const ws = createMockWebSocket();

      const handler = createMessageHandler({
        ws,
        ...services,
        onAuthSuccess,
      });

      const event = {
        data: JSON.stringify({
          type: 'queue_listed',
          queues: [{ id: 1, name: 'Queue 1' }],
        }),
      } as MessageEvent;

      handler(event);

      expect(services.taskService.handleMessage).toHaveBeenCalledTimes(1);
      expect(services.queueService.handleMessage).toHaveBeenCalledTimes(1);
      expect(services.providerService.handleMessage).toHaveBeenCalledTimes(1);
    });
  });

  describe('プロバイダーメッセージの処理', () => {
    it('provider_listedメッセージを受信したら各サービスのhandleMessageが呼ばれる', () => {
      const services = createMockServices();
      const onAuthSuccess = vi.fn();
      const ws = createMockWebSocket();

      const handler = createMessageHandler({
        ws,
        ...services,
        onAuthSuccess,
      });

      const event = {
        data: JSON.stringify({
          type: 'provider_listed',
          providers: [
            { id: '1', name: 'Provider 1', issuer: 'issuer', audience: 'audience', idps: [] },
          ],
        }),
      } as MessageEvent;

      handler(event);

      expect(services.taskService.handleMessage).toHaveBeenCalledTimes(1);
      expect(services.queueService.handleMessage).toHaveBeenCalledTimes(1);
      expect(services.providerService.handleMessage).toHaveBeenCalledTimes(1);
    });
  });
});
