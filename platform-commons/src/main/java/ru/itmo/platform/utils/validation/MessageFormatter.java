package ru.itmo.platform.utils.validation;

public interface MessageFormatter {

  String format(ValidationException exception);

}
