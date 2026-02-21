import {useContext} from "react";
import {ServiceContext} from "./ServiceContext";
import type {
    AuthMessageService,
    ProviderMessageService,
    QueueMessageService,
    TaskMessageService,
    TokenApiService,
} from "../api";

export function useAuthMessageService(): AuthMessageService | null {
    const context = useContext(ServiceContext);
    if (!context) return null;
    return context.authMessageService;
}

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

export function useTokenApiService(): TokenApiService | null {
    const context = useContext(ServiceContext);
    if (!context) return null;
    return context.tokenApiService;
}
