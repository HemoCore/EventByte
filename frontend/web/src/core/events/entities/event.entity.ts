import { createEntityAdapter } from "@reduxjs/toolkit";

export type VenueEntity = {
    name: string
    adresse: string
    coordinates: { latitude: number, longitude: number }
}

export type EventEntity = {
    id: string
    title: string
    cover: string
    likedCount: number
    venue: VenueEntity,
    isSoldOut: boolean
    lowestPrice: number
    startAt: string
    description: string
}

export const eventsAdapter = createEntityAdapter<EventEntity>();

export const eventEntityBuilder = (event: EventEntity = {
    id: '1',
    title: 'Event 1',
    cover: 'https://cover.com',
    likedCount: 0,
    venue: venueEntityBuilder().build(),
    isSoldOut: false,
    lowestPrice: 0,
    startAt: new Date("2026-01-01").toISOString(),
    description: 'Description'
}) => ({
    withId: (id: string) => eventEntityBuilder({ ...event, id }),
    withTitle: (title: string) => eventEntityBuilder({ ...event, title }),
    withCover: (cover: string) => eventEntityBuilder({ ...event, cover }),
    withLikedCount: (likedCount: number) => eventEntityBuilder({ ...event, likedCount }),
    withVenue: (venue: VenueEntity) => eventEntityBuilder({ ...event, venue }),
    soldOut: () => eventEntityBuilder({ ...event, isSoldOut: true }),
    withLowestPrice: (lowestPrice: number) => eventEntityBuilder({ ...event, lowestPrice }),
    startAt: (startAt: Date) => eventEntityBuilder({ ...event, startAt: startAt.toISOString() }),
    withDescription: (description: string) => eventEntityBuilder({ ...event, description }),
    build: () => event,
})
export const venueEntityBuilder = (venue: VenueEntity = {
    adresse: 'Place de la Bourse',
    name: 'Bourse',
    coordinates: { latitude: 0, longitude: 0 }
}) => ({
    withName: (name: string) => venueEntityBuilder({ ...venue, name }),
    withAdresse: (adresse: string) => venueEntityBuilder({ ...venue, adresse }),
    withCoordinates: (coordinates: { latitude: number, longitude: number }) => venueEntityBuilder({ ...venue, coordinates }),
    build: () => venue
})
