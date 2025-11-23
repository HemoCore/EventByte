import {
    AuthGateway,
    AuthUser,
    OnAuthStateChangeCallback,
} from "../ports/auth.gateway";

const LOCAL_STORAGE_CURRENT_USER_KEY = "CURRENT_USER";

export class AsyncStorageAuthGateway implements AuthGateway {
    private onAuthStateChangedCallback?: OnAuthStateChangeCallback;

    constructor(private readonly authGateway: AuthGateway) { }

    login() {
        this.authGateway.login();
    }

    logout() {
        this.authGateway.logout();
    }

    onAuthStateChange(callback: OnAuthStateChangeCallback) {
        this.onAuthStateChangedCallback = function(user) {
            if (user) {
                this.saveCurrentUser(user);
            } else {
                this.removeCurrentUser();
            }
            callback(user);
        };
        this.checkAuth();
        return this.authGateway.onAuthStateChange(
            this.onAuthStateChangedCallback.bind(this),
        );
    }

    private async getCurrentUser() {
        const user = localStorage.getItem(LOCAL_STORAGE_CURRENT_USER_KEY);
        if (user) {
            return JSON.parse(user);
        }
        return undefined;
    }

    private saveCurrentUser(user: AuthUser) {
        localStorage.setItem(LOCAL_STORAGE_CURRENT_USER_KEY, JSON.stringify(user));
    }

    removeCurrentUser() {
        localStorage.removeItem(LOCAL_STORAGE_CURRENT_USER_KEY);
    }

    private async checkAuth() {
        const user = await this.getCurrentUser();
        this.onAuthStateChangedCallback?.(user);
    }

}
