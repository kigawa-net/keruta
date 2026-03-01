import {useContext} from "react";
import {ServiceContext} from "./ServiceContext";
import type {TaskMessageService,} from "../../../components/api";

export function useTaskMessageService(): TaskMessageService | null {
    const context = useContext(ServiceContext);
    if (!context) return null;
    return context.taskMessageService;
}
