package com.example.assist.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.assist.factory.AmazonS3Factory;
import com.example.assist.model.SuggestionRequest;
import com.google.common.util.concurrent.RateLimiter;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class S3ControllerTest {

    private final SuggestionRequest request = SuggestionRequest.builder().content("Test").build();
    private final String BUCKET_NAME = "aijobhuntlogbucket";

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private AmazonS3Factory s3Factory;

    @Mock
    private RateLimiter rateLimiter;

    private S3Controller s3Controller;

    @BeforeEach
    void init() {
        when(s3Factory.getObject()).thenReturn(s3Client);
        when(rateLimiter.tryAcquire()).thenReturn(true);
        s3Controller = new S3Controller(s3Factory, rateLimiter);
    }

    @Test
    void test_uploadToS3_success() {
        when(s3Client.putObject(any(), any(), any(), any())).thenReturn(new PutObjectResult());
        s3Controller.uploadToS3(request);
        verify(s3Client, times(1)).putObject(eq(BUCKET_NAME), anyString(), any(), any());
    }

    @Test
    void test_uploadToS3_error_noThrow() {
        when(s3Client.putObject(anyString(), anyString(), any(), any())).thenThrow(new IllegalArgumentException());
        s3Controller.uploadToS3(request);
    }
}
