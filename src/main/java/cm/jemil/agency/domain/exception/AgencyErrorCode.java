package cm.jemil.agency.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgencyErrorCode {
    AGENCY_404_001("AGENCY_404_001", "Agency not found"),
    AGENCY_404_002("AGENCY_404_002", "Demo not found"),
    AGENCY_400_001("AGENCY_400_001", "Invalid agency state"),
    AGENCY_400_002("AGENCY_400_002", "Not enough seats available");

    private final String code;
    private final String message;
}
