package cm.jemil.trip.adapter.inbound.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import cm.jemil.generated.ticket.adapter.rest.inbound.api.DemoApi;
import cm.jemil.generated.ticket.adapter.rest.inbound.dto.CreateDemoDTO;
import cm.jemil.generated.ticket.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.ticket.adapter.rest.inbound.dto.DemoDTO;
import cm.jemil.trip.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.trip.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.trip.application.inbound.usecase.GetDemoByIdUserCase;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("tripDemoController")
@RequestMapping("/trip")
@RequiredArgsConstructor
public class DemoController implements DemoApi {

    private final CreateDemoUserCase createDemoUserCase;
    private final GetAllDemoUserCase getAllDemoUserCase;
    private final GetDemoByIdUserCase getDemoByIdUseCase;
    private final TripDemoRestMapper demoRestMapper;

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
