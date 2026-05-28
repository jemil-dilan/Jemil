package cm.jemil.ticket.adapter.inbound.rest;

import cm.jemil.generated.ticket.adapter.rest.inbound.dto.CreateDemoDTO;
import cm.jemil.generated.ticket.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.ticket.adapter.rest.inbound.dto.DemoDTO;
import cm.jemil.ticket.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.ticket.demo.view.DemoView;
import cm.jemil.ticket.demo.view.DemoView.DemoView1;
import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {IdMappers.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DemoRestMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name")
    CreateDemoUserCase.CreateDemoCommand toCreateDemo(CreateDemoDTO createDemoDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "newId", source = "value")
    CreationResponseDTO fromDomain(UUID value);

    default DemoDTO fromDomain(DemoView demoView) {
        return switch (demoView) {
            case DemoView1 demoView1 -> new DemoDTO().id(demoView1.demoId()).name(demoView1.demoName());
        };
    }
}
