import { createTestStore } from "@/core/create-store";
import { stateBuilder } from "@/core/state-builder";
import { describe, expect, it } from "vitest";
import { selectTicketCardViewmodel } from "./ticket-card.viewmodel";
import { eventEntityBuilder, venueEntityBuilder } from "@/core/events/entities/event.entity";
import { bookingEntityBuilder } from "@/core/booking/entities/booking.entity";
import { bookingLineEntityBuilder } from "@/core/booking/entities/booking-line.entity";

describe('ticket card viewmodel', () => {
    it('retrieve ticket details', () => {
        const now = new Date("2025-01-01")
        const store = createTestStore({}, stateBuilder()
            .withEvents([
                eventEntityBuilder().withId('1')
                    .withTitle('Title')
                    .withCover('https://example.com/event-cover.jpg')
                    .startAt(new Date('2025-01-02'))
                    .withVenue(venueEntityBuilder()
                        .withName('Bourse')
                        .withAdresse('Place de la bourse')
                        .build()).build()
            ])
            .withBooking(bookingEntityBuilder().withId('1').withEventId('1')
                .withLines([
                    bookingLineEntityBuilder().withQuantity(2).build(),
                    bookingLineEntityBuilder().withQuantity(1).build(),
                ]).build())
            .build())
        const viewmodel = selectTicketCardViewmodel({ now, id: '1' })(store.getState())

        expect(viewmodel).toEqual({
            id: '1',
            event: {
                title: 'Title',
                cover: 'https://example.com/event-cover.jpg',
                date: 'Jan 2, 2025',
                location: 'Bourse, Place de la bourse',
            },
            quantity: 3,
            isUpcoming: true
        })
    })
})
