package com.cosain.trilo.trip.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(of = {"fileName"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class TripImage {

    public static final String DEFAULT_IMAGE_NAME = "trip-default-image-png";
    private static final TripImage DEFAULT_IMAGE = new TripImage(DEFAULT_IMAGE_NAME);

    @Column(name = "image-file-name")
    private String fileName;

    public static TripImage of(String fileName) {
        if (fileName.equals(DEFAULT_IMAGE_NAME)) {
            return DEFAULT_IMAGE;
        }
        return new TripImage(fileName);
    }

    public static TripImage defaultImage() {
        return DEFAULT_IMAGE;
    }

    private TripImage(String fileName) {
        this.fileName = fileName;
    }
}
