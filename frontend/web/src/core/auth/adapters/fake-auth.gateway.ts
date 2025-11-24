import {
    AuthGateway,
    AuthUser,
    OnAuthStateChangeCallback,
} from "../ports/auth.gateway";

export class FakeAuthGateway implements AuthGateway {
    loginRequested = false;
    logoutRequested = false;
    logUserWith?: AuthUser;
    public onAuthStateChangeCallback: OnAuthStateChangeCallback | null = null;

    login() {
        this.loginRequested = true;
        this.simulateAuthStateChange(this.logUserWith);
    }

    logout(): void {
        this.logoutRequested = true;
        this.simulateAuthStateChange();
    }

    onAuthStateChange(callback: OnAuthStateChangeCallback) {
        this.onAuthStateChangeCallback = callback;
        return () => {
            this.onAuthStateChangeCallback = null;
        };
    }

    simulateAuthStateChange(user?: AuthUser) {
        this.onAuthStateChangeCallback?.(user);
    }
}
