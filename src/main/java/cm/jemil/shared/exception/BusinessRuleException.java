package cm.jemil.shared.exception;

public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String message, String code) {
        super(message, code);
    }
}
