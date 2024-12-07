package com.mango.mangoprofileservice.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Configuration
public class CloudBucketConfig {

    @Value("${gcp.project.id}")
    private String projectId;

    @Value("${gcp.config.file}")
    private String config;

    @Bean
    @SneakyThrows
    public Storage storage() {
        InputStream inputStream = new ClassPathResource(config).getInputStream();
        StorageOptions storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build();

        return storage.getService();
    }
}
