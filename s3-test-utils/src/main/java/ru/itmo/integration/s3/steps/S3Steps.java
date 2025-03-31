package ru.itmo.integration.s3.steps;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Steps {

  private final S3Client s3Client;

  public S3Steps(List<S3Client> s3Clients) {
    this.s3Client = s3Clients.get(0);
  }

  @Given("Minio buckets exist")
  public void createMinioBuckets(List<String> buckets) {
    for (String bucketName : buckets) {
      boolean bucketExists = s3Client.listBuckets().buckets().stream()
          .anyMatch(bucket -> bucket.name().equals(bucketName));
      if (!bucketExists) {
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
      }
    }
  }

  @SneakyThrows
  @Given("S3 bucket {string} contains files")
  public void addFilesToS3(String bucket, DataTable dataTable) {
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

    for (Map<String, String> row : rows) {
      String from = row.get("from");
      String to = row.get("to");
      Resource resource = resolver.getResource("classpath:" + from);
      try (InputStream is = resource.getInputStream()) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(to)
            .build();

        RequestBody requestBody = RequestBody.fromInputStream(is, is.available());
        s3Client.putObject(putRequest, requestBody);
      }
    }
  }

  @Given("Minio buckets are empty")
  public void clearMinioBuckets(List<String> buckets) {
    for (String bucketName : buckets) {
      ListObjectsResponse objects = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());

      for (S3Object object : objects.contents()) {
        String key = object.key();
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
      }
    }
  }

  @Then("File {string} exists in bucket {string} in {long} millis")
  public void fileInBucketExists(String fileName, String bucketName, long timeoutMillis) {
    await().atMost(timeoutMillis, TimeUnit.MILLISECONDS)
        .until(() -> s3HasFileInBucket(fileName, bucketName));
  }

  private boolean s3HasFileInBucket(String filename, String bucket) {
    return s3Client.listObjects(
            ListObjectsRequest.builder().bucket(bucket).build()
        ).contents().stream()
        .anyMatch(file -> file.key().equals(filename));
  }

}