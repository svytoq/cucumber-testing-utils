package ru.itmo.platform.utils.system;

import java.util.UUID;

public interface TraceIdProvider {

  UUID traceId();

}