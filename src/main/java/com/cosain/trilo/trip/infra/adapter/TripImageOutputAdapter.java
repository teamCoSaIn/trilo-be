package com.cosain.trilo.trip.infra.adapter;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.trip.application.exception.TripImageUploadFailedException;
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

    public String uploadImage(ImageFile imageFile, String uploadFileName) {
        ObjectMetadata objectMetadata = makeMetaData(imageFile);
        try {
            amazonS3.putObject(bucketName, uploadFileName, imageFile.getInputStream(), objectMetadata);
        } catch (SdkClientException e) {
            log.error("[S3] 여행 이미지 업로드 실패", e);
            throw new TripImageUploadFailedException("[S3] 여행 이미지 업로드 실패", e);
        }
        return getTripImageFullPath(uploadFileName);
    }

    /**
     * 여행 이미지의 전체 경로(URL)을 얻어옵니다.
     * @param uploadedFileName
     * @return
     */
    public String getTripImageFullPath(String uploadedFileName) {
        return bucketPath.concat(uploadedFileName);
    }

    private ObjectMetadata makeMetaData(ImageFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

}
