import {useContext} from "react";
import {ServiceContext} from "./ServiceContext";
import type {ProviderMessageService, QueueMessageService, TaskMessageService,} from "../../../components/api";

export function useTaskMessageService(): TaskMessageService | null {
    const context = useContext(ServiceContext);
    if (!context) return null;
    return context.taskMessageService;
}

export function useQueueMessageService(): QueueMessageService | null {
    const context = useContext(ServiceContext);
    if (!context) return null;
    return context.queueMessageService;
}

export function useProviderMessageService(): ProviderMessageService | null {
    const context = useContext(ServiceContext);
    if (!context) return null;
    return context.providerMessageService;
}
