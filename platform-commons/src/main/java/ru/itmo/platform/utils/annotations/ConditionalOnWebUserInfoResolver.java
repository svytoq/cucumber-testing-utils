package ru.itmo.platform.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnExpression(
    "T(org.apache.commons.lang3.StringUtils).isBlank('${platform.utils.system.userInfoResolver:}')")
public @interface ConditionalOnWebUserInfoResolver {

}