package fr.soft_labs.eventbyte.domain.models.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void converts_euros_to_cents() {
        assertThat(Money.fromEuros(new BigDecimal("27.51")).cents()).isEqualTo(2751);
    }

    @Test
    void rounds_to_the_nearest_cent() {
        assertThat(Money.fromEuros(new BigDecimal("10.005")).cents()).isEqualTo(1001);
    }

    @Test
    void accepts_a_price_without_decimals() {
        assertThat(Money.fromEuros(new BigDecimal("25")).cents()).isEqualTo(2500);
    }

    @Test
    void rejects_a_negative_amount() {
        assertThatThrownBy(() -> Money.fromEuros(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}