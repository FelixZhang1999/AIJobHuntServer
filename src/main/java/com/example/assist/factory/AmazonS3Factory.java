package com.example.assist.factory;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Inject AmazonS3Client.
 */
@Component
public class AmazonS3Factory implements FactoryBean<AmazonS3> {

    @Value("${s3.region}")
    private String region;

    @Override
    public AmazonS3 getObject() {
        return AmazonS3ClientBuilder.standard()
                                    .withRegion(region)
                                    .build();
    }

    @Override
    public Class<?> getObjectType() {
        return AmazonS3.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
