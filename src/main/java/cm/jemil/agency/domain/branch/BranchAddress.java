package cm.jemil.agency.domain.branch;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.BRANCH_400_003;

import cm.jemil.shared.exception.DomainException;


/**
 * Value object representing the address of an agency branch.
 * Enforces business rules: branch address cannot be null or blank.
 */
public record BranchAddress(String value) {
    public BranchAddress {
        // Defense in depth: validate even though provides compile-time safety
        if (value == null) {
            throw new DomainException(BRANCH_400_003, "Branch address is required");
        }
        if (value.isBlank()) {
            throw new DomainException(BRANCH_400_003, "Branch address cannot be blank");
        }
    }
}


