package cm.jemil.shared.utils;

import java.util.List;

public record PaginationResponseData<T>(List<T> elements, int count, boolean condition) {}
