import { AxiosInstance } from "axios"
import { LikesGateway } from "../ports/likes.gateway"

export class ApiLikesGateway implements LikesGateway {
	constructor(private readonly client: AxiosInstance) {}

	async likeEvent(eventId: string): Promise<void> {
		await this.client.post(`/events/${eventId}/like`)
	}

	async unlikeEvent(eventId: string): Promise<void> {
		await this.client.delete(`/events/${eventId}/like`)
	}
}
