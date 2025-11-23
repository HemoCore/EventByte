import Keycloak from "keycloak-js";
import {
    AuthGateway,
    AuthUser,
    OnAuthStateChangeCallback,
} from "../ports/auth.gateway";

export class KeycloakAuthGateway implements AuthGateway {
    private callbacks: OnAuthStateChangeCallback[] = [];

    constructor(private readonly keycloak: Keycloak) { }

    private async init() {
        this.keycloak.onAuthRefreshError = () => {
            this.notifyAuthStateChange(undefined);
        }


        this.keycloak.onAuthSuccess = () => {
            this.notifyAuthStateChange(this.getUserFromToken());
        }

        await this.keycloak.init({
            onLoad: 'check-sso',
            silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        })

        this.notifyAuthStateChange(this.getUserFromToken());
    }

    onAuthStateChange(callback: OnAuthStateChangeCallback): () => void {
        this.callbacks.push(callback);

        this.init();

        return () => {
            this.callbacks = this.callbacks.filter(cb => cb !== callback);
        };
    }

    login() {
        this.keycloak.login();
    }

    logout(): void {
        this.keycloak.logout();
    }

    private getUserFromToken(): AuthUser | undefined {
        if (!this.keycloak.tokenParsed) return undefined;

        const token = this.keycloak.tokenParsed;
        return {
            id: token.sub!,
            email: token.email,
            fullname: token.name,
        };
    }

    private notifyAuthStateChange(user?: AuthUser): void {
        this.callbacks.forEach(callback => callback(user));
    }
}
