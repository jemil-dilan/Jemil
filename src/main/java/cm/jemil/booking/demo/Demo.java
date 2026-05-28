package cm.jemil.booking.demo;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Demo {

    private DemoId id;

    private DemoName name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Demo demo)) {
            return false;
        }
        return Objects.equals(id, demo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    Demo(DemoId id, DemoName name) {
        this.id = id;
        this.name = name;
    }

    public UUID id() {
        return this.id.value();
    }

    public static Demo of(DemoId id, DemoName name) {
        return new Demo(id, name);
    }

    public DemoCreatedEvent toCreatedEvent() {
        return new DemoCreatedEvent(this.id, this.name);
    }
}
