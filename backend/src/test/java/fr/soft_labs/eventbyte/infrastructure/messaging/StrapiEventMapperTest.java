package fr.soft_labs.eventbyte.infrastructure.messaging;

import fr.soft_labs.eventbyte.domain.models.event.Event;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StrapiEventMapperTest {

    @Test
    void converts_a_strapi_message_into_an_event_with_prices_in_cents() {
        StrapiEventMessage message = new StrapiEventMessage(
                "evt_01a20dce",
                "Brunch Musical gourmand",
                "Brunch with live music",
                "https://res.cloudinary.com/demo/aa.jpg",
                null,
                new StrapiEventMessage.VenueMessage("Restaurant Prosacco",
                        "111 Place Voltaire, 31000 Toulouse", 43.626, 1.455),
                List.of(
                        new StrapiEventMessage.TicketMessage("tkt_1", "Standard", 21, new BigDecimal("27.51")),
                        new StrapiEventMessage.TicketMessage("tkt_2", "Premium", 78, new BigDecimal("43.39"))
                )
        );

        Event event = StrapiEventMapper.toDomain(message);

        assertThat(event.id()).isEqualTo("evt_01a20dce");
        assertThat(event.venue().address()).isEqualTo("111 Place Voltaire, 31000 Toulouse");
        assertThat(event.tickets())
                .extracting(ticket -> ticket.price().cents())
                .containsExactly(2751L, 4339L);
    }
}
