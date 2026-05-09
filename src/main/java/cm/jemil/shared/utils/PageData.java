package cm.jemil.shared.utils;

import java.util.List;
import java.util.stream.Stream;

public record PageData<T>(long total, List<T> elements) {

  public Stream<T> stream() {
    return elements.stream();
  }
}
