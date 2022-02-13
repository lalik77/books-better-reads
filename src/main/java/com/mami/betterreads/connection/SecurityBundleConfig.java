package com.mami.betterreads.connection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class SecurityBundleConfig {

  @Value("${datastax.astra.secure-connect-bundle}")
  private String path;

  public Resource loadFileWithClassPathResource() {

    return new ClassPathResource(path);

  }
}
