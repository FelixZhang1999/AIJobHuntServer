package com.example.assist.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.assist.factory.AmazonS3Factory;
import com.example.assist.model.SuggestionRequest;
import com.google.common.util.concurrent.RateLimiter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S3Controller {

    private final static String BUCKET_NAME = "aijobhuntlogbucket";
    private final static String FILE_KEY = "suggestions/";
    private final AmazonS3 s3Client;
    private final Logger logger = LoggerFactory.getLogger(S3Controller.class);
    private final RateLimiter rateLimiter;

    public S3Controller(final AmazonS3Factory s3Factory,
                        final RateLimiter rateLimiter) {
        this.s3Client = s3Factory.getObject();
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/sendsuggestion")
    public void uploadToS3(@RequestBody final SuggestionRequest itemContent) {
        if (!rateLimiter.tryAcquire()) {
            return;
        }
        final Date date = Calendar.getInstance().getTime();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        final String fileDir = FILE_KEY + dateFormat.format(date) + ".txt";

        final InputStream inputStream = new ByteArrayInputStream(itemContent.getContent().getBytes());

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(itemContent.getContent().getBytes().length);
        metadata.setContentType("text/plain");

        try {
            s3Client.putObject(BUCKET_NAME, fileDir, inputStream, metadata);
            logger.info("Success put object in S3.");
        } catch (final Exception e) {
            logger.info("S3 put object error: " + e.getMessage());
        }
    }
}