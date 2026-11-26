package fr.soft_labs.eventbyte.domain.models.event;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(long cents) {

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("Un montant ne peut pas être négatif : " + cents);
        }
    }

    public static Money ofCents(long cents) {
        return new Money(cents);
    }

    public static Money fromEuros(BigDecimal euros) {
        if (euros == null) {
            throw new IllegalArgumentException("Le prix est obligatoire");
        }
        return new Money(euros.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact());
    }

    /** The amount in euros, e.g. 1535 cents → 15.35. Only for the JSON answers: inside, everything stays in cents. */
    public BigDecimal euros() {
        return BigDecimal.valueOf(cents, 2);
    }

    public boolean isLessThan(Money other) {
        return cents < other.cents;
    }
}