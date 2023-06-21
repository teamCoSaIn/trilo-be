package com.cosain.trilo.trip.infra.adapter;

import com.amazonaws.services.s3.AmazonS3;
import com.cosain.trilo.common.file.ImageFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TripImageOutputAdapter {

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String bucketPath;

    public TripImageOutputAdapter(
            AmazonS3 s3Client,
            @Value("${cloud.aws.s3.bucket-name}") String bucketName,
            @Value("${cloud.aws.s3.bucket-path}") String bucketPath) {
        this.amazonS3 = s3Client;
        this.bucketName = bucketName;
        this.bucketPath = bucketPath;
    }

    public String uploadImage(ImageFile file, String uploadFileName) {
        return null;
    }

}
