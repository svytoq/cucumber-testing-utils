package ru.itmo.platform.utils.strings;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

  public static String beanName(String baseName, String beanName) {
    return normalizeBeanName(baseName + "_" + beanName);
  }

  public static String normalizeBeanName(String beanName) {
    String[] parts = beanName.split("_");
    StringBuilder sb = new StringBuilder(parts[0].toLowerCase());

    for (int i = 1; i < parts.length; i++) {
      sb.append(capitalizeFirstToLowerLast(parts[i]));
    }

    return sb.toString();
  }

  public static String capitalizeFirst(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  public static String capitalizeFirstToLowerLast(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }

}