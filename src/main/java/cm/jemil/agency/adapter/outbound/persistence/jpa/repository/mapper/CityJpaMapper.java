package cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.CityJpa;
import cm.jemil.agency.domain.city.City;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.views.CityView.CityView1;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CityJpaMapper {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "name", source = "name")
    CityJpa toJpa(City city);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    City toDomain(CityJpa entity);

    default CityId mapToCityId(UUID id) {
        return id != null ? new CityId(id) : null;
    }

    default UUID map(CityId id) {
        return id != null ? id.value() : null;
    }

    default CityView1 toCityView1(CityJpa entity) {
        if (entity == null) {
            return null;
        }
        return new CityView1(entity.getId(), entity.getName());
    }
}
