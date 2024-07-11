package com.project.supershop.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {
    @Bean
    public AmazonS3 s3() {
        // AKIA4MTWKTH6TAA3SYGR
        // USAMFcqltwrQPYBa/UiG9HoPwmNfCnIMm8pd7T2n
        AWSCredentials awsCredentials =
                new BasicAWSCredentials("AKIA4MTWKTH6TAA3SYGR", "USAMFcqltwrQPYBa/UiG9HoPwmNfCnIMm8pd7T2n");
        return AmazonS3ClientBuilder
                .standard()
                .withRegion("ap-south-1")
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
