export type LikesGateway = {
    likeEvent: (eventId: string) => Promise<void>
    unlikeEvent: (eventId: string) => Promise<void>
}
