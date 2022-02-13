package com.mami.betterreads;

import com.mami.betterreads.connection.SecurityBundleConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileCopyUtils;

@SpringBootApplication
public class BetterReadsApplication {

	public static void main(String[] args)  {
		SpringApplication.run(BetterReadsApplication.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(
			SecurityBundleConfig securityBundleConfig)
			throws IOException {

		File file = File.createTempFile("test", ".txt");
		final InputStream inputStream = securityBundleConfig.loadFileWithClassPathResource()
				.getInputStream();
		OutputStream outputStream = new FileOutputStream(file);
		FileCopyUtils.copy(inputStream, outputStream);
		final Path path = file.toPath();
		return builder -> builder.withCloudSecureConnectBundle(path);
	}
}
