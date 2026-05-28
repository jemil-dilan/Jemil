package cm.jemil.ticket.demo;

public record DemoName(String value) {

    public static DemoName from(String value) {
        return new DemoName(value);
    }
}
