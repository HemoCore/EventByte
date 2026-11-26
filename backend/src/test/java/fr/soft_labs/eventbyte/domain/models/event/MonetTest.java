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
    void converts_cents_to_euros() {
        assertThat(Money.ofCents(1535).euros()).isEqualTo(new BigDecimal("15.35"));
    }

    @Test
    void euros_keep_two_decimals() {
        assertThat(Money.ofCents(2500).euros()).isEqualTo(new BigDecimal("25.00"));
        assertThat(Money.ofCents(5).euros()).isEqualTo(new BigDecimal("0.05"));
        assertThat(Money.ofCents(0).euros()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void euros_and_back_give_the_same_cents() {
        assertThat(Money.fromEuros(Money.ofCents(2751).euros()).cents()).isEqualTo(2751);
    }

    @Test
    void rejects_a_negative_amount() {
        assertThatThrownBy(() -> Money.fromEuros(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}