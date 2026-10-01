package cm.nyi.shared.utils;

import java.util.List;

public record PaginationResponseData<T>(List<T> elements, int count, boolean condition) {}
