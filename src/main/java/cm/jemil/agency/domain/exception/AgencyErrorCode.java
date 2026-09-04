package cm.jemil.agency.domain.exception;

import cm.jemil.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgencyErrorCode implements ErrorCode {
    AGENCY_404_001("AGENCY_404_001", "Agency not found"),
    AGENCY_400_001("AGENCY_400_001", "Invalid agency state"),
    AGENCY_400_002("AGENCY_400_002", "Not enough seats available"),
    AGENCY_400_003("AGENCY_400_003", "Agency name is required"),
    AGENCY_400_004("AGENCY_400_004", "Address is required"),
    AGENCY_400_005("AGENCY_400_005", "Phone number is required"),
    AGENCY_400_006("AGENCY_400_006", "Route cities are required"),
    AGENCY_400_007("AGENCY_400_007", "Route price must be positive"),
    AGENCY_400_008("AGENCY_400_008", "Seat count must be positive"),
    AGENCY_400_009("AGENCY_400_009", "Departure time is required"),
    AGENCY_400_010("AGENCY_400_010", "Invalid commission rate"),
    AGENCY_400_011("AGENCY_400_011", "License number is required"),
    AGENCY_400_012("AGENCY_400_012", "Origin and destination cannot be the same"),
    CITY_400_001("CITY_400_001", "City name is required"),
    CITY_400_002("CITY_400_002", "City region is required"),
    CITY_404_001("CITY_404_001", "City not found"),
    BRANCH_400_001("BRANCH_400_001", "Branch name is required"),
    BRANCH_400_002("BRANCH_400_002", "Branch name already exists for this agency"),
    BRANCH_400_003("BRANCH_400_003", "Branch address is required"),
    BRANCH_404_001("BRANCH_404_001", "Branch not found");

    private final String code;
    private final String message;
}
