package com.cosain.trilo.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * 여행이미지 VO(값 객체)입니다.
 */
@Getter
@EqualsAndHashCode(of = {"fileName"})
@ToString(of = {"fileName"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TripImage {

    /**
     * 여행이미지의 디폴트 파일명
     */
    private static final String DEFAULT_IMAGE_NAME = "trips/trip-default-image.png";

    /**
     * 여행이미지 디폴트 VO(값 객체)
     */
    private static final TripImage DEFAULT_IMAGE = new TripImage(DEFAULT_IMAGE_NAME);

    /**
     * 여행이미지의 파일명
     */
    @Column(name = "trip_image_file_name")
    private String fileName;

    /**
     * 여행이미지 VO(값 객체)를 생성합니다.
     * @param fileName : 이미지 파일명
     * @return : 여행 이미지 VO(값 객체)
     */
    public static TripImage of(String fileName) {
        return fileName.equals(DEFAULT_IMAGE_NAME)
                ? DEFAULT_IMAGE
                : new TripImage(fileName);
    }

    /**
     * 여행이미지 디폴트 VO(값 객체)를 반환합니다.
     * @return 여행 이미지 디폴트 VO(값 객체)
     * @see TripImage#DEFAULT_IMAGE
     */
    public static TripImage defaultImage() {
        return DEFAULT_IMAGE;
    }

    private TripImage(String fileName) {
        this.fileName = fileName;
    }
}
