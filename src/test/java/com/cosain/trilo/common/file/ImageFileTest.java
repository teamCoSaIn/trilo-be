package com.cosain.trilo.common.file;

import com.cosain.trilo.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@DisplayName("이미지 파일 테스트")
public class ImageFileTest {

    @Test
    @DisplayName("실제 jpeg -> 정상 생성됨")
    public void testRealJpegImage() throws IOException {
        String name = "image";
        String fileName = "test-jpeg-image.jpeg";
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpeg";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        ImageFile imageFile = ImageFile.from(multipartFile);

        assertThat(imageFile.getOriginalFileName()).isEqualTo(fileName);
        assertThat(imageFile.getContentType()).isEqualTo(contentType);
        assertThat(imageFile.getInputStream()).isNotEmpty();
        assertThat(imageFile.getExt()).isEqualTo("jpeg");
        assertThat(imageFile.getSize()).isGreaterThan(0);
    }

    @Test
    @DisplayName("실제 gif -> 정상 생성됨")
    public void testRealGifImage() throws IOException {
        String name = "image";
        String fileName = "test-gif-image.gif";
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/gif";


        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        ImageFile imageFile = ImageFile.from(multipartFile);

        assertThat(imageFile.getOriginalFileName()).isEqualTo(fileName);
        assertThat(imageFile.getContentType()).isEqualTo(contentType);
        assertThat(imageFile.getInputStream()).isNotEmpty();
        assertThat(imageFile.getExt()).isEqualTo("gif");
        assertThat(imageFile.getSize()).isGreaterThan(0);
    }

    @Test
    @DisplayName("실제 png -> 정상 생성됨")
    public void testRealPngImage() throws IOException {
        String name = "image";
        String fileName = "test-png-image.png";
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/png";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        ImageFile imageFile = ImageFile.from(multipartFile);

        assertThat(imageFile.getOriginalFileName()).isEqualTo(fileName);
        assertThat(imageFile.getContentType()).isEqualTo(contentType);
        assertThat(imageFile.getInputStream()).isNotEmpty();
        assertThat(imageFile.getSize()).isGreaterThan(0);
    }

    @Test
    @DisplayName("실제 webp -> 정상 생성됨")
    public void testRealWebpImage() throws IOException {
        String name = "image";
        String fileName = "test-webp-image.webp";
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/webp";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        ImageFile imageFile = ImageFile.from(multipartFile);

        assertThat(imageFile.getOriginalFileName()).isEqualTo(fileName);
        assertThat(imageFile.getContentType()).isEqualTo(contentType);
        assertThat(imageFile.getInputStream()).isNotEmpty();
        assertThat(imageFile.getSize()).isGreaterThan(0);
    }

    @Test
    @DisplayName("MultipartFile이 null -> EmptyFileException 발생")
    public void nullTest() {
        assertThatThrownBy(() -> ImageFile.from(null))
                .isInstanceOf(EmptyFileException.class);
    }

    @Test
    @DisplayName("내용물이 비어있는 파일 -> EmptyFileException 발생")
    public void emptyFile() throws IOException {
        // given
        String name = "image";
        String fileName = "empty-file.jpg";
        String filePath = "src/test/resources/testFiles/"+fileName;

        FileInputStream fileInputStream = new FileInputStream(filePath);
        MockMultipartFile multipartFile = new MockMultipartFile(name, fileInputStream);

        // when & then
        assertThatThrownBy(() -> ImageFile.from(multipartFile))
                .isInstanceOf(EmptyFileException.class);
    }

    @Test
    @DisplayName("MultipartFile에 파일 이름 없음 -> NoFileNameException 발생")
    public void noFileName() throws IOException {
        String name = "image";
        String fileName = "test-jpeg-image.jpeg";
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);

        // MultipartFile에 파일 이름 정보가 전달되지 않은 특수상황 가정
        MockMultipartFile multipartFile = new MockMultipartFile(name, fileInputStream);
        assertThatThrownBy(() -> ImageFile.from(multipartFile))
                .isInstanceOf(NoFileNameException.class);
    }

    @Test
    @DisplayName("확장자 없음 -> NoFileExtensionException 발생")
    public void noFileExtension() throws IOException {
        String name = "image";
        String fileName = "no-extension"; // 확장자 없음
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "application/octet-stream";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        assertThatThrownBy(() -> ImageFile.from(multipartFile))
                .isInstanceOf(NoFileExtensionException.class);
    }

    @Test
    @DisplayName("이미지 파일 확장자 아님 -> NotImageFileExtensionException 발생")
    public void notImageFileExtension() throws IOException {
        String name = "image";
        String fileName = "not-image-extension.txt"; // 확장자가 이미지가 아님
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "text/plain";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        assertThatThrownBy(() -> ImageFile.from(multipartFile))
                .isInstanceOf(NotImageFileExtensionException.class);
    }

    @Test
    @DisplayName("이미지 아님 -> NotImageFileException 발생")
    public void noImageFile() throws IOException {
        String name = "image";
        String fileName = "no-image.jpg"; // 확장자는 jpg인데 실제로 이미지가 아님
        String filePath = "src/test/resources/testFiles/"+fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpg";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        assertThatThrownBy(() -> ImageFile.from(multipartFile))
                .isInstanceOf(NotImageFileException.class);
    }
}
