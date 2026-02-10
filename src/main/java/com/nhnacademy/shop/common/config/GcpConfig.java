package com.nhnacademy.shop.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GcpConfig {


    @Profile(value = "dev")
    @Bean
    public Storage storageDev(
            @Value("${gcp.gcs.credentials.location}") Resource keyFile
    ) throws IOException {
        try (InputStream is = keyFile.getInputStream()) {
            return StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build()
                    .getService();
        }
    }

    @Profile(value = "prod")
    @Bean
    public Storage storageProd() {
        return StorageOptions.getDefaultInstance().getService();
    }


}

