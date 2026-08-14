package cm.jemil.agency.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class CityNotFoundException extends DomainException {

    public CityNotFoundException() {
        super(AgencyErrorCode.CITY_404_001);
    }
}
