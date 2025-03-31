package ru.itmo.platform.utils.system;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemInfoProvider {

  private final DateTimeProvider dateTimeProvider;
  private final UserInfoResolver userInfoResolver;
  private final TraceIdProvider traceIdProvider;

  public Instant now() {
    return dateTimeProvider.now();
  }

  public String userLogin() {
    return userInfoResolver.userLogin();
  }

  public UUID traceId() {
    return traceIdProvider.traceId();
  }

}