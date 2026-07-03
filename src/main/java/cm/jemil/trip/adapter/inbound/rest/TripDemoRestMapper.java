package cm.jemil.trip.adapter.inbound.rest;

import cm.jemil.generated.ticket.adapter.rest.inbound.dto.CreateDemoDTO;
import cm.jemil.generated.ticket.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.ticket.adapter.rest.inbound.dto.DemoDTO;
import cm.jemil.trip.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.trip.demo.view.DemoView;
import java.util.UUID;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripDemoRestMapper {

    default DemoDTO fromDomain(DemoView view) {
        if (view instanceof DemoView.DemoView1 v) {
            return fromDomain(v);
        }
        throw new IllegalArgumentException("Unknown view type");
    }

    @Mapping(target = "id", source = "demoId")
    @Mapping(target = "name", source = "demoName")
    DemoDTO fromDomain(DemoView.DemoView1 view);

    CreateDemoUseCase.CreateDemoCommand toCreateDemo(CreateDemoDTO dto);

    default CreationResponseDTO fromDomain(UUID id) {
        var response = new CreationResponseDTO();
        response.setNewId(id);
        return response;
    }
}
