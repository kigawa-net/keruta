import { createContext, ReactNode, useContext, useMemo } from "react";
import { TaskService, QueueService, ProviderService } from "../../services/domain";
import {
  useTaskMessageService,
  useQueueMessageService,
  useProviderMessageService,
} from "../ServiceContext";

interface DomainServices {
  taskService: TaskService;
  queueService: QueueService;
  providerService: ProviderService;
}

const DomainContext = createContext<DomainServices | null>(null);

export function DomainServiceProvider({ children }: { children: ReactNode }) {
  const taskMsgService = useTaskMessageService();
  const queueMsgService = useQueueMessageService();
  const providerMsgService = useProviderMessageService();

  const services = useMemo(
    () => ({
      taskService: new TaskService(taskMsgService),
      queueService: new QueueService(queueMsgService),
      providerService: new ProviderService(providerMsgService),
    }),
    [taskMsgService, queueMsgService, providerMsgService]
  );

  return <DomainContext.Provider value={services}>{children}</DomainContext.Provider>;
}

export function useDomainServices(): DomainServices {
  const services = useContext(DomainContext);
  if (!services) throw new Error("useDomainServices must be used within DomainServiceProvider");
  return services;
}

export function useTaskService(): TaskService {
  return useDomainServices().taskService;
}

export function useQueueService(): QueueService {
  return useDomainServices().queueService;
}

export function useProviderService(): ProviderService {
  return useDomainServices().providerService;
}
