package ru.itmo.platform.utils.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("ips-dev")
@PropertySource("classpath:environment.properties")
public class LocalConfiguration {

}