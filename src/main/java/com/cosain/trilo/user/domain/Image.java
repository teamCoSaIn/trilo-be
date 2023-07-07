package com.cosain.trilo.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = {"baseURL", "fileName"})
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Embeddable
public class Image {

    private final static String defaultMyPageImageName = "defaultBadge";

    @Column
    private final String baseURL;
    @Column
    private final String filaName;

    private Image(final String baseURL,final String fileName) {
        this.baseURL = baseURL;
        this.filaName = fileName;
    }

    public static Image of(final String baseURL,final String fileName){
        return new Image(baseURL, fileName);
    }

    public static Image initializeMyPageImage(final String baseURL){
        return new Image(baseURL, defaultMyPageImageName);
    }
}
