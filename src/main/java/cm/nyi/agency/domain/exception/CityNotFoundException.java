package cm.nyi.agency.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class CityNotFoundException extends DomainException {

    public CityNotFoundException() {
        super(AgencyErrorCode.CITY_404_001);
    }
}
