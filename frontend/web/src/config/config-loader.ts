import type { AppConfig } from "./config-registry";
import { configRegistry } from "./config-registry";
import { EnvConfig } from "../lib/env";

export async function loadConfig(env: EnvConfig): Promise<AppConfig> {
    const configLoader = configRegistry.get(env.configMode);

    if (!configLoader) {
        throw new Error(
            `No configuration found for mode: ${env.configMode}. Available modes: ${configRegistry.getAvailableConfigs().join(", ")}`,
        );
    }

    const { config } = await configLoader();
    return config(env);
}
