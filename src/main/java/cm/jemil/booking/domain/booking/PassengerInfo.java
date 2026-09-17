package cm.jemil.booking.domain.booking;

import cm.jemil.shared.utils.PhoneNumber;
import java.util.Objects;

/**
 * Value object representing passenger information.
 */
public record PassengerInfo(String name, PhoneNumber phoneNumber) {

    public PassengerInfo {
        Objects.requireNonNull(name, "Passenger name cannot be null");
        Objects.requireNonNull(phoneNumber, "Passenger phone number cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Passenger name cannot be blank");
        }

        if (phoneNumber.number() == null || phoneNumber.number().isBlank()) {
            throw new IllegalArgumentException("Passenger phone number cannot be blank");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassengerInfo that = (PassengerInfo) o;
        return Objects.equals(name, that.name) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber);
    }
}
