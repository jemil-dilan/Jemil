package cm.jemil.auth.adapter.inbound.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import cm.jemil.auth.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.auth.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.auth.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.generated.agency.adapter.rest.inbound.api.DemoApi;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateDemoDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.DemoDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController implements DemoApi {

    private final CreateDemoUserCase createDemoUserCase;
    private final GetAllDemoUserCase getAllDemoUserCase;
    private final GetDemoByIdUserCase getDemoByIdUseCase;
    private final DemoRestMapper demoRestMapper;

    @Override
    public ResponseEntity<List<DemoDTO>> fetchAllDemo(String fieldsToExtractCode) {
        var result = getAllDemoUserCase.execute().stream()
                .map(demoRestMapper::fromDomain)
                .toList();
        return ResponseEntity.status(OK).body(result);
    }

    @Override
    public ResponseEntity<DemoDTO> fetchDemoById(UUID demoId, String fieldsToExtractCode) {

        var response = getDemoByIdUseCase.execute(new GetDemoByIdUserCase.DemoQuery(demoId, fieldsToExtractCode));
        return ResponseEntity.ok(demoRestMapper.fromDomain(response));
    }

    @Override
    public ResponseEntity<CreationResponseDTO> createDemo(CreateDemoDTO demoDTO) {
        var newDemoId = demoRestMapper.fromDomain(createDemoUserCase.execute(demoRestMapper.toCreateDemo(demoDTO)));
        return ResponseEntity.status(CREATED).body(newDemoId);
    }
}
