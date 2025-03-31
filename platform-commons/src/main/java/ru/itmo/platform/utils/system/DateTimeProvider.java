package ru.itmo.platform.utils.system;

import java.time.Instant;

public interface DateTimeProvider {

  Instant now();

}