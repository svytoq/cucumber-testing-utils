package ru.itmo.integration.s3.configuration;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.MinIOContainer;

@Component
@SuppressWarnings("java:S2095")
public class MinioInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private final MinIOContainer minIOContainer;

  public MinioInitializer() {
    this.minIOContainer = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
        .withReuse(true)
        .withExposedPorts(9000)
        .withUserName("minioadmin")
        .withPassword("minioadmin")
        .withStartupTimeout(Duration.ofSeconds(60));
  }

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    minIOContainer.start();
    TestPropertyValues.of(
        "S3_SERVER_URL: " + minIOContainer.getS3URL()
    ).applyTo(configurableApplicationContext.getEnvironment());
  }

  @PreDestroy
  public void stopContainer() {
    minIOContainer.stop();
  }

}