package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.DemoJpa;
import cm.jemil.booking.demo.Demo;
import cm.jemil.booking.demo.DemoId;
import cm.jemil.booking.demo.DemoName;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingDemoJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name.value")
    DemoJpa fromDomain(Demo demo);

    default Demo toDomain(DemoJpa value) {
        return Demo.of(new DemoId(value.getId()), new DemoName(value.getName()));
    }
}
