package cm.jemil.agency.adapter.inbound.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import cm.jemil.agency.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.agency.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.generated.agency.adapter.rest.inbound.api.DemoApi;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateDemoDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.DemoDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("agencyDemoController")
@RequestMapping("/agency")
@RequiredArgsConstructor
public class DemoController implements DemoApi {

    private final CreateDemoUseCase createDemoUseCase;
    private final GetAllDemoUseCase getAllDemoUseCase;
    private final GetDemoByIdUseCase getDemoByIdUseCase;
    private final AgencyDemoRestMapper demoRestMapper;

    @Override
    public ResponseEntity<List<DemoDTO>> fetchAllDemo(String fieldsToExtractCode) {
        var result = getAllDemoUseCase.execute().stream()
                .map(demoRestMapper::fromDomain)
                .toList();
        return ResponseEntity.status(OK).body(result);
    }

    @Override
    public ResponseEntity<DemoDTO> fetchDemoById(UUID demoId, String fieldsToExtractCode) {

        var response = getDemoByIdUseCase.execute(new GetDemoByIdUseCase.DemoQuery(demoId, fieldsToExtractCode));
        return ResponseEntity.ok(demoRestMapper.fromDomain(response));
    }

    @Override
    public ResponseEntity<CreationResponseDTO> createDemo(CreateDemoDTO demoDTO) {
        var newDemoId = demoRestMapper.fromDomain(createDemoUseCase.execute(demoRestMapper.toCreateDemo(demoDTO)));
        return ResponseEntity.status(CREATED).body(newDemoId);
    }
}
