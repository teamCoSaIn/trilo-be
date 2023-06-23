package com.cosain.trilo.unit.trip.infra.adapter;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.trip.application.exception.TripImageUploadFailedException;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("TripImageOutputAdapter 테스트")
public class TripImageOutputAdapterTest {

    private static final String TEST_RESOURCE_PATH = "src/test/resources/testFiles/";

    private TripImageOutputAdapter tripImageOutputAdapter;

    private AmazonS3 amazonS3;
     private String bucketName;
    private String bucketPath;

    @BeforeEach
    public void setUp() {
        amazonS3 = mock(AmazonS3.class);
        bucketName = "bucketName";
        bucketPath = "https://bucketPath.com/";
        tripImageOutputAdapter = new TripImageOutputAdapter(amazonS3, bucketName, bucketPath);
    }

    @Test
    @DisplayName("이미지 업로드 성공 테스트")
    void testSuccess() throws Exception{
        ImageFile imageFile = imageFileFixture("test-jpeg-image.jpeg");

        String fileName = "xxx/{랜덤 uuid}.jpeg";

        tripImageOutputAdapter.uploadImage(imageFile, fileName);

        // then
        verify(amazonS3, times(1)).putObject(eq(bucketName), eq(fileName), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("업로드 과정 SdkClientException 발생 -> TestImageUploadFailedException 발생")
    void testSdkClientExceptionFailed() throws Exception{
        ImageFile imageFile = imageFileFixture("test-jpeg-image.jpeg");

        String fileName = "xxx/{랜덤 uuid}.jpeg";

        given(amazonS3.putObject(eq(bucketName),eq(fileName),any(InputStream.class), any(ObjectMetadata.class)))
                .willThrow(SdkClientException.class);

        // when & then
        assertThatThrownBy(() -> tripImageOutputAdapter.uploadImage(imageFile, fileName))
                .isInstanceOf(TripImageUploadFailedException.class);
        verify(amazonS3, times(1)).putObject(eq(bucketName), eq(fileName), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("getTripImageFullPath -> 여행 이미지의 전체 경로를 얻어온다.")
    void testGetTripImageFullPath() {
        // given
        String fileName = "trips/1/12760-fa712554-123.jpeg";

        // when
        String fullImagePath = tripImageOutputAdapter.getTripImageFullPath(fileName);

        // then
        assertThat(fullImagePath).isEqualTo(bucketPath.concat(fileName));
    }

    private ImageFile imageFileFixture(String testImageResourceFileName) throws IOException {
        String name = "image";
        String filePath = TEST_RESOURCE_PATH + testImageResourceFileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpeg";

        MockMultipartFile multipartFile = new MockMultipartFile(name, testImageResourceFileName, contentType, fileInputStream);
        return ImageFile.from(multipartFile);
    }
}
