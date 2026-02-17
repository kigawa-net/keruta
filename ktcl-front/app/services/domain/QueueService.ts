import { QueueMessageService } from "../api/MessageServices";
import { ReceiveMsg } from "../../msg/msg";
import { ClientQueueCreatedMsg, ClientQueueListedMsg } from "../../msg/queue";
import { Queue, CreateQueueInput } from "./QueueTypes";
import { validateQueueName } from "./QueueUtils";

/** キュー関連のビジネスロジックを管理するドメインサービス */
export class QueueService {
  private queueListCallbacks: Set<(queues: Queue[]) => void> = new Set();
  private queueCreatedCallbacks: Set<(queueId: number) => void> = new Set();

  constructor(private queueMessageService: QueueMessageService) {}

  createQueue(input: CreateQueueInput): void {
    const nameValidation = validateQueueName(input.name);
    if (!nameValidation.valid) throw new Error(nameValidation.error);
    if (input.providerId <= 0) throw new Error("Invalid provider ID");
    this.queueMessageService.createQueue(input.providerId, input.name.trim());
  }

  listQueues(): void {
    this.queueMessageService.listQueues();
  }

  handleMessage(message: ReceiveMsg): void {
    switch (message.type) {
      case "queue_listed": this.handleQueueListed(message as ClientQueueListedMsg); break;
      case "queue_created": this.handleQueueCreated(message as ClientQueueCreatedMsg); break;
    }
  }

  onQueueList(callback: (queues: Queue[]) => void): () => void {
    this.queueListCallbacks.add(callback);
    return () => this.queueListCallbacks.delete(callback);
  }

  onQueueCreated(callback: (queueId: number) => void): () => void {
    this.queueCreatedCallbacks.add(callback);
    return () => this.queueCreatedCallbacks.delete(callback);
  }

  private handleQueueListed(message: ClientQueueListedMsg): void {
    const queues: Queue[] = message.queues.map((q) => ({ id: q.id, name: q.name }));
    this.queueListCallbacks.forEach((cb) => cb(queues));
  }

  private handleQueueCreated(message: ClientQueueCreatedMsg): void {
    this.queueCreatedCallbacks.forEach((cb) => cb(message.queueId));
  }
}
