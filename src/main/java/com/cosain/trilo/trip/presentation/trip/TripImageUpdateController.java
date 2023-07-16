package com.cosain.trilo.trip.presentation.trip;

import com.cosain.trilo.auth.infra.jwt.UserPayload;
import com.cosain.trilo.auth.presentation.Login;
import com.cosain.trilo.auth.presentation.LoginUser;
import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.trip.application.trip.service.TripImageUpdateService;
import com.cosain.trilo.trip.presentation.trip.dto.response.TripImageUpdateResponse;
import com.cosain.trilo.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TripImageUpdateController {

    private final TripImageUpdateService tripImageUpdateService;

    /**
     * 여행의 이미지를 변경하고, 저장된 이미지 경로를 응답합니다.
     * @param userPayload : 토큰 페이로드
     * @param tripId : 여행 식별자(id)
     * @param multipartFile : 파일
     * @return 응답 API (여행 식별자, 이미지 경로)
     */
    @PostMapping("/api/trips/{tripId}/image/update")
    @ResponseStatus(HttpStatus.OK)
    @Login
    public TripImageUpdateResponse updateTripImage(
            @LoginUser UserPayload userPayload,
            @PathVariable Long tripId,
            @RequestParam("image") MultipartFile multipartFile) {

        Long tripperId = userPayload.getId();
        ImageFile imageFile = ImageFile.from(multipartFile);

        String imageURL = tripImageUpdateService.updateTripImage(tripId, tripperId, imageFile);
        return new TripImageUpdateResponse(tripId, imageURL);
    }

}
