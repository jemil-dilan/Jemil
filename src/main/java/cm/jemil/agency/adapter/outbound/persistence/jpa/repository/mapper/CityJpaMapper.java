package cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.CityJpa;
import cm.jemil.agency.domain.city.City;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CityJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name.value")
    @Mapping(target = "region", source = "region.value")
    CityJpa toJpa(City city);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "name.value", source = "name")
    @Mapping(target = "region.value", source = "region")
    City toDomain(CityJpa entity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "name.value", source = "name")
    @Mapping(target = "region.value", source = "region")
    CityView1 toCityView1(CityJpa entity);
}
