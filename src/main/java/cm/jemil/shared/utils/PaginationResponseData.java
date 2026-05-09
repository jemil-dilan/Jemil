package cm.jemil.shared.utils;


import cm.jemil.auth.domain.demo.view.DemoView;
import cm.jemil.auth.domain.demo.view.DemoView.DemoView1;

import java.util.List;

public record PaginationResponseData(
    List<DemoView1> collectors, int count, boolean hasCollector) {}
