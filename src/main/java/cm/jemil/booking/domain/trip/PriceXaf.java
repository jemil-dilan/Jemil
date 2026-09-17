package cm.jemil.booking.domain.trip;

import java.util.Objects;

/**
 * Value object representing a price in XAF (Central African Franc).
 * Since XAF has no minor unit, we use integer values.
 */
public record PriceXaf(int amount) {

    public PriceXaf {
        if (amount < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public PriceXaf multiply(int quantity) {
        return new PriceXaf(Math.multiplyExact(amount, quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceXaf that = (PriceXaf) o;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return String.valueOf(amount);
    }
}
