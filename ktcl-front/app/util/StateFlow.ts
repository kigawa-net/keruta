import {useEffect} from "react";

export interface StateFlow<T> {
    addListener(listener: (value: T) => void): number;

    removeListener(id: number): void;
}

export class MutableStateFlow<T> implements StateFlow<T> {
    static nextListenerId = 0;
    private readonly listeners: Record<number, (value: T) => void> = {}

    addListener(listener: (value: T) => void): number {
        const id = MutableStateFlow.nextListenerId++
        this.listeners[id] = listener;
        return id
    }

    removeListener(id: number): void {
        delete this.listeners[id]
    }

    call(value: T) {
        for (const listener of Object.values(this.listeners)) {
            listener(value)
        }
    }
}

export function useStateFlow<T>(flow: StateFlow<T> | undefined, listener: (value: T) => void) {
    useEffect(() => {
        if (flow === undefined) return
        const id = flow.addListener(listener)
        return () => flow.removeListener(id)
    }, [flow]);
}
