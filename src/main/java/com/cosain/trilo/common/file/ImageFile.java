package com.cosain.trilo.common.file;

import com.cosain.trilo.common.exception.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ImageFile {

    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Set.of("jpg","jpeg", "gif", "png", "webp"));
    private static final Set<String> MIME_TYPES = new HashSet<>(Set.of("image/jpg", "image/gif", "image/jpeg", "image/png", "image/webp"));

    private static final Tika tika = new Tika();

    private String originalFileName;

    private String ext;

    private MultipartFile multipartFile;

    public static ImageFile from(MultipartFile multipartFile) {
        requiredNotEmptyAndNotNull(multipartFile); // null 이거나 비어있는 file이면 예외 발생

        String originalFileName = multipartFile.getOriginalFilename();
        validateOriginalFileName(originalFileName); // 파일 이름이 누락됐다면 예외 발생

        String ext = extractExt(originalFileName);
        validateExtension(ext); // 확장자가 비어있거나, 이미지 파일의 확장자가 아니면 예외 발생
        validateMimeType(multipartFile); // 실제 파일을 분석해서 이미지 파일이 아니면 예외 발생

        return ImageFile.builder()
                .originalFileName(originalFileName)
                .ext(ext)
                .multipartFile(multipartFile)
                .build();
    }

    /**
     * 파일을 검증합니다.
     * 파일이 null 이거나, 파일의 내용이 없으면 예외를 발생시킵니다.
     * @param multipartFile : 파일(MultipartFile)
     */
    private static void requiredNotEmptyAndNotNull(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.info("파일이 없거나 비어있음");
            throw new EmptyFileException("비어있거나 파일의 내용이 없음");
        }
    }

    /**
     * 파일의 이름을 검증합니다. 파일이 이름이 없으면 예외가 발생합니다.
     * @param originalFileName : 파일 이름
     */
    private static void validateOriginalFileName(String originalFileName) {
        if (!StringUtils.hasText(originalFileName)) {
            log.info("파일 이름 없음");
            throw new NoFileNameException("파일 이름 없음");
        }
    }

    /**
     * 파일의 확장자를 추출합니다.
     * @param originalFileName : 파일 이름
     * @return
     */
    private static String extractExt(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf(".");

        if (dotIndex == -1) {
            return "";
        }
        return originalFileName.substring(dotIndex + 1);
    }

    /**
     * 파일의 확장자의 존재 하는지, 그리고 확장자가 존재한다면 이미지 확장자인지 검증합니다.
     * @param ext : 파일의 확장자
     */
    private static void validateExtension(String ext) {
        log.info("확장자 : {}", ext);
        if (!StringUtils.hasText(ext)) {
            log.info("파일 확장자 누락");
            throw new NoFileExtensionException("파일 확장자 누락!");
        }
        if (!IMAGE_EXTENSIONS.contains(ext)) {
            log.info("이미지 파일의 확장자가 아님");
            throw new NotImageFileExtensionException("이미지 파일의 확장자가 아님");
        }
    }

    /**
     * 파일을 실제로 확인하여, 이미지 파일이 맞는 지 검증합니다.
     * @param multipartFile : 파일
     */
    private static void validateMimeType(MultipartFile multipartFile) {
        try {
            String mimeType = tika.detect(multipartFile.getInputStream());
            log.info("mimeType = {}", mimeType);
            if (!MIME_TYPES.contains(mimeType)) {
                throw new NotImageFileException("이미지 파일이 아닙니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ImageFile(String originalFileName, String ext, MultipartFile multipartFile) {
        this.originalFileName = originalFileName;
        this.ext = ext.toLowerCase();
        this.multipartFile = multipartFile;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getExt() {
        return ext;
    }

    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    public long getSize() {
        return multipartFile.getSize();
    }

    public String getContentType() {
        return multipartFile.getContentType();
    }

}
