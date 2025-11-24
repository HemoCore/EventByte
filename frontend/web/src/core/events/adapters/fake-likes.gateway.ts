import { LikesGateway } from "../ports/likes.gateway"

export class FakeLikesGateway implements LikesGateway {
    private _likedEvents: Map<string, boolean> = new Map()
    willLikeFail: boolean = false
    willUnlikeFail: boolean = false

    constructor(private delay: number = 0) { }

    async likeEvent(eventId: string) {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        if (this.willLikeFail) {
            throw new Error('Like failed')
        }
        this._likedEvents.set(eventId, true)
    }

    async unlikeEvent(eventId: string) {
        await new Promise(resolve => setTimeout(resolve, this.delay))
        if (this.willUnlikeFail) {
            throw new Error('Like failed')
        }
        this._likedEvents.delete(eventId)
    }

    get likedEvents(): string[] {
        return [...this._likedEvents.keys()]
    }
}
