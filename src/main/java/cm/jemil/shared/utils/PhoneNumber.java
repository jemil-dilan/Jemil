package cm.jemil.shared.utils;

public record PhoneNumber(String countryCode, String number) {
    public String fullNumber() {
        return "+" + countryCode + number;
    }
}
