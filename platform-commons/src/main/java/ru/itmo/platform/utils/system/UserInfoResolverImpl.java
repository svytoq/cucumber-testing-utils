package ru.itmo.platform.utils.system;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import ru.itmo.platform.utils.annotations.ConditionalOnWebUserInfoResolver;


@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnWebUserInfoResolver
public class UserInfoResolverImpl implements UserInfoResolver {

  private final NativeWebRequest webRequest;

  @Override
  public String userLogin() {
    String authorization = webRequest.getHeader(AUTHORIZATION);

    try {
      return parseJwt(authorization).optString(FIELD_LOGIN, Constants.UNKNOWN);
    } catch (RuntimeException e) {
      log.error("Failed to resolve user. Returning UNKNOWN", e);
      return Constants.UNKNOWN;
    }
  }

  @Override
  public Map<String, String> userInfo(String authorization) {
    Map<String, String> map = new HashMap<>();

    JSONObject jwt = parseJwt(authorization);

    map.put(FIELD_LOGIN, jwt.optString(FIELD_LOGIN, Constants.UNKNOWN));
    map.put(FIELD_NAME, jwt.optString(FIELD_NAME, Constants.UNKNOWN));

    return map;
  }

  @SneakyThrows
  private JSONObject parseJwt(String authorization) {
    if (authorization == null) {
      return new JSONObject();
    }

    String decoded = new String(Base64.getUrlDecoder().decode(requireNonNull(authorization).split("\\.")[1]), UTF_8);
    return new JSONObject(decoded);
  }

}