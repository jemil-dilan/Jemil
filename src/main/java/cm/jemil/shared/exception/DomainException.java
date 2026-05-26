package cm.jemil.shared.exception;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {
  private final String code;

  protected DomainException(String message, String code) {
    super(message);
    this.code = code;
  }
}
