package com.cosain.trilo.trip.application.trip.command.usecase;

import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripImageUploadFailedException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;

public interface TripImageUpdateUseCase {

    /**
     * 여행의 이미지를 수정합니다.
     * @param tripId : 여행의 식별자
     * @param tripperId : 사용자(여행자)의 식별자
     * @param file : 이미지 파일
     * @return 교체된 이미지의 전체 경로 URL
     * @throws TripNotFoundException : 일치하는 식별자의 여행을 찾을 수 없을 경우 발생
     * @throws NoTripUpdateAuthorityException : 여행을 수정할 권한이 없을 때 발생
     * @throws TripImageUploadFailedException : 여행의 이미지를 이미지 저장소에 올리는데 실패했을 때 발생
     */
    String updateTripImage(Long tripId, Long tripperId, ImageFile file)
            throws TripNotFoundException, NoTripUpdateAuthorityException, TripImageUploadFailedException;

}
