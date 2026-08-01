package cm.jemil.agency.e2e.step;

import java.util.UUID;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
@Data
public class ScenarioContext {

    private UUID createdAgencyId;
    private UUID createdRouteId;
    private String requestBody;
    private int responseStatus;
    private String responseBody;
}
