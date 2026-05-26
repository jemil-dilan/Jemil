package cm.jemil.payment.adapter.inbound.rest;

import cm.jemil.payment.adapter.inbound.rest.IdMappers;
import cm.jemil.payment.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.payment.demo.view.DemoView;
import cm.jemil.payment.demo.view.DemoView.DemoView1;
import cm.milou.generated.jemil.payment.adapter.rest.inbound.dto.CreateDemoDTO;
import cm.milou.generated.jemil.payment.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.milou.generated.jemil.payment.adapter.rest.inbound.dto.DemoDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

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

  default  DemoDTO fromDomain(DemoView demoView) {
    return switch (demoView) {
      case DemoView1 demoView1 -> new DemoDTO().id(demoView1.demoId()).name(demoView1.demoName());
    };
  }
}
