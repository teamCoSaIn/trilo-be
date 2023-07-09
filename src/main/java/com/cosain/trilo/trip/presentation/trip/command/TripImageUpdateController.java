package com.cosain.trilo.trip.presentation.trip.command;

import com.cosain.trilo.common.LoginUser;
import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.trip.application.trip.command.service.TripImageUpdateService;
import com.cosain.trilo.trip.presentation.trip.command.dto.response.TripImageUpdateResponse;
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
     * @param user : 사용자
     * @param tripId : 여행 식별자(id)
     * @param multipartFile : 파일
     * @return 응답 API (여행 식별자, 이미지 경로)
     */
    @PostMapping("/api/trips/{tripId}/image/update")
    @ResponseStatus(HttpStatus.OK)
    public TripImageUpdateResponse updateTripImage(
            @LoginUser User user,
            @PathVariable Long tripId,
            @RequestParam("image") MultipartFile multipartFile) {

        Long tripperId = user.getId();
        ImageFile imageFile = ImageFile.from(multipartFile);

        String imageURL = tripImageUpdateService.updateTripImage(tripId, tripperId, imageFile);
        return new TripImageUpdateResponse(tripId, imageURL);
    }

}
