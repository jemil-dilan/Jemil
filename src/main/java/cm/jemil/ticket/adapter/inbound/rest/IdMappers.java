package cm.jemil.ticket.adapter.inbound.rest;

import cm.jemil.ticket.demo.DemoId;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IdMappers {

    @Nullable default UUID toUuid(@Nullable DemoId id) {
        return Optional.ofNullable(id).map(DemoId::value).orElse(null);
    }

    @Nullable default DemoId uuidToDemoId(@Nullable UUID uuid) {
        return Optional.ofNullable(uuid).map(DemoId::new).orElse(null);
    }
}
