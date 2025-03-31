package ru.itmo.platform.utils.io;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

  private final String originalFileName;
  private final String contentType;
  private final byte[] bytes;

  public CustomMultipartFile(String originalFileName, String contentType, byte[] bytes) {
    this.originalFileName = requireNonNull(originalFileName, "File name is blank");
    this.contentType      = requireNonNull(contentType,      "ContentType is blank");
    this.bytes            = requireNonNull(bytes,            "File is empty");
  }

  @NonNull
  @Override
  public String getName() {
    return getOriginalFilename();
  }

  @NonNull
  @Override
  public String getOriginalFilename() {
    return originalFileName;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return bytes == null || bytes.length == 0;
  }

  @Override
  public long getSize() {
    return bytes.length;
  }

  @NonNull
  @Override
  public byte[] getBytes() {
    return bytes;
  }

  @NonNull
  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public void transferTo(@NonNull File destination) throws IOException, IllegalStateException {
    try (FileOutputStream fos = new FileOutputStream(destination)) {
      fos.write(bytes);
    }
  }

}