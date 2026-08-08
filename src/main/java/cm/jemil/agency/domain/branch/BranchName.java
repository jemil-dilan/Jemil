package cm.jemil.agency.domain.branch;

import static cm.jemil.agency.domain.exception.AgencyErrorCode.BRANCH_400_001;

import cm.jemil.shared.exception.DomainException;


/**
 * Value object representing the name of an agency branch.
 * Enforces business rules: branch name cannot be null or blank.
 */
public record BranchName(String value) {
    public BranchName {
        // Defense in depth: validate even though provides compile-time safety
        if (value == null) {
            throw new DomainException(BRANCH_400_001, "Branch name is required");
        }
        if (value.isBlank()) {
            throw new DomainException(BRANCH_400_001, "Branch name cannot be blank");
        }
    }
}


