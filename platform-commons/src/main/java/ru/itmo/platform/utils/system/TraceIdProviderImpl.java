package ru.itmo.platform.utils.system;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TraceIdProviderImpl implements TraceIdProvider {

  @Override
  public UUID traceId() {
    return UUID.randomUUID();
  }

}