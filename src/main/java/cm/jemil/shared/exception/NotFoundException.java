package cm.jemil.shared.exception;

public class NotFoundException extends DomainException {
  public NotFoundException(String message) {
    super(message, "NOT_FOUND");
  }

  public NotFoundException(String message, String code) {
    super(message, code);
  }
}
