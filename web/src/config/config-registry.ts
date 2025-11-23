import type { Dependencies } from "@/core/create-store";
import type { loadStripe } from "@stripe/stripe-js";
import type { EnvConfig } from "../lib/env";

export type AppConfig = {
    dependencies: Dependencies;
    stripe: NonNullable<Awaited<ReturnType<typeof loadStripe>>>;
};

export type ConfigFactory = (env: EnvConfig) => Promise<AppConfig>;

type ConfigLoader = () => Promise<{ config: ConfigFactory }>;

const createConfigRegistry = (registry: Record<string, ConfigLoader> = {}) => {
    return {
        get: (mode: string): ConfigLoader | undefined => registry[mode],
        getAvailableConfigs: (): string[] => Object.keys(registry),
        has: (mode: string): boolean => mode in registry,
    }
}

export const configRegistry = createConfigRegistry({
    demo: () => import("./demo.config"),
    production: () => import("./prod.config"),
});
