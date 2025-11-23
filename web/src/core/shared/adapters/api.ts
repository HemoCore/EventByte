import axios from "axios";

type ApiConfig = {
    apiUrl: string;
    getToken: () => string | undefined;
}

export const createApiClient = ({ apiUrl, getToken }: ApiConfig) => {
    const apiClient = axios.create({
        baseURL: apiUrl,
    });

    apiClient.interceptors.request.use((config) => {
        const token = getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });

    return apiClient;
}
