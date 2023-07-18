package com.cosain.trilo.trip.application.trip.service.trip_image_update;

import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripImageUploadFailedException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripImage;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TripImageUpdateService {

    private final TripRepository tripRepository;
    private final TripImageOutputAdapter tripImageOutputAdapter;

    /**
     * 여행의 이미지를 수정합니다.
     * @param tripId : 여행의 식별자
     * @param tripperId : 사용자(여행자)의 식별자
     * @param file : 이미지 파일
     * @return 교체된 이미지의 전체 URL(경로)
     * @throws TripNotFoundException : 일치하는 식별자의 여행을 찾을 수 없을 경우 발생
     * @throws NoTripUpdateAuthorityException : 여행을 수정할 권한이 없을 때 발생
     * @throws TripImageUploadFailedException : 여행의 이미지를 이미지 저장소에 올리는데 실패했을 때 발생
     */
    @Transactional
    public String updateTripImage(Long tripId, Long tripperId, ImageFile file)
            throws TripNotFoundException, NoTripUpdateAuthorityException, TripImageUploadFailedException {

        Trip trip = findTrip(tripId);

        validateTripUpdateAuthority(trip, tripperId); // 이미지를 수정할 권한이 있는 지 검증

        String uploadName = makeUploadFileName(tripId, file); // 이미지 저장소에 올릴 이름 구성
        tripImageOutputAdapter.uploadImage(file, uploadName); // 이미지 저장소에 업로드 후, 전체 이미지 경로(fullPath)를 구성

        trip.changeImage(TripImage.of(uploadName)); // 여행이미지 도메인의 실제 이미지 변경
        return tripImageOutputAdapter.getFullTripImageURL(uploadName); // 이미지 전체 경로를 반환
    }

    /**
     * 여행을 조회해옵니다. 일치하는 식별자의 여행이 없으면 예외가 발생합니다.
     * @param tripId : 여행의 식별자
     * @return : 여행
     * @throws TripNotFoundException : 일치하는 식별자의 여행이 없을 때 발생
     */
    private Trip findTrip(Long tripId) throws TripNotFoundException {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip 없음"));
    }

    /**
     * 사용자가 해당 여행을 수정할 권한이 있는 지 검증합니다.
     * @param trip : 여행
     * @param tripperId : 사용자의 식별자(id)
     * @throws NoTripUpdateAuthorityException : 여행을 수정할 권한이 없을 때 발생
     */
    private void validateTripUpdateAuthority(Trip trip, Long tripperId) throws NoTripUpdateAuthorityException {
        if (!trip.getTripperId().equals(tripperId)) {
            throw new NoTripUpdateAuthorityException("Trip을 수정할 권한이 없음");
        }
    }

    /**
     * 전달받은 이미지의 저장 파일명을 구성합니다.
     * @param tripId : 여행 식별자(id)
     * @param file : 이미지 파일
     * @return : 여행의 저장 파일명
     */
    private static String makeUploadFileName(Long tripId, ImageFile file) {
        return String.format("trips/%d/%s.%s",
                tripId, UUID.randomUUID(), file.getExt());
    }

}
