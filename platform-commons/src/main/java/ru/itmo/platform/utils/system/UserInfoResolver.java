package ru.itmo.platform.utils.system;

import java.util.Map;

public interface UserInfoResolver {

  String FIELD_LOGIN = "preferred_username";
  String FIELD_NAME = "name";

  String userLogin();

  Map<String, String> userInfo(String authorization);

}