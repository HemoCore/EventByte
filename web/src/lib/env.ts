export type EnvConfig = {
    configMode: string;
    apiUrl: string;
    keycloak: {
        url: string;
        realm: string;
        clientId: string;
    };
    stripe: {
        publicKey: string;
        clientSecret: string;
    };
};

function getEnvVar(key: string, defaultValue?: string): string {
    const value = import.meta.env[key];

    if (!value && !defaultValue) {
        throw new Error(
            `Missing required environment variable: ${key}. Please check your .env file.`,
        );
    }

    return value || defaultValue!;
}


export const getEnv = (): EnvConfig => ({
    configMode: getEnvVar("VITE_CONFIG_MODE", "dev"),
    apiUrl: getEnvVar("VITE_API_URL", "http://localhost:3000"),
    keycloak: {
        url: getEnvVar("VITE_KEYCLOAK_URL", "http://localhost:8180"),
        realm: getEnvVar("VITE_KEYCLOAK_REALM", "eventbyte"),
        clientId: getEnvVar("VITE_KEYCLOAK_CLIENT_ID", "web-app"),
    },
    stripe: {
        publicKey: getEnvVar(
            "VITE_STRIPE_PUBLIC_KEY",
        ),
        clientSecret: getEnvVar(
            "VITE_STRIPE_CLIENT_SECRET",
        ),
    },
});
