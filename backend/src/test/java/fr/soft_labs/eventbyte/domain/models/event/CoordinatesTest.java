package fr.soft_labs.eventbyte.domain.models.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class CoordinatesTest {

    private static final Coordinates PARIS = new Coordinates(48.8585, 2.3475);
    private static final Coordinates LYON = new Coordinates(45.7640, 4.8357);

    @Test
    void the_distance_from_paris_to_lyon_is_about_392_km() {
        assertThat(PARIS.distanceKmTo(LYON)).isCloseTo(392, within(3.0));
    }

    @Test
    void the_distance_to_itself_is_zero() {
        assertThat(PARIS.distanceKmTo(PARIS)).isZero();
    }

    @Test
    void rejects_an_invalid_latitude() {
        assertThatThrownBy(() -> new Coordinates(91, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejects_an_invalid_longitude() {
        assertThatThrownBy(() -> new Coordinates(48, 181)).isInstanceOf(IllegalArgumentException.class);
    }
}
