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

    public boolean isLessThan(Money other) {
        return cents < other.cents;
    }
}