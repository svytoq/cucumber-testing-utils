package ru.itmo.platform.utils.system;

import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@ConditionalOnProperty(value = {"ips.platform.utils.system.userInfoResolver"}, havingValue = "system")
public class SystemUserInfoResolver implements UserInfoResolver {

  private static final String SYSTEM_USER = "system";

  @Override
  public String userLogin() {
    return SYSTEM_USER;
  }

  @Override
  public Map<String, String> userInfo(String authorization) {
    return Map.of(
        FIELD_LOGIN, SYSTEM_USER,
        FIELD_NAME, SYSTEM_USER
    );
  }

}