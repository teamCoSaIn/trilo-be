package com.cosain.trilo.unit.trip.application.trip.service;

import com.cosain.trilo.common.file.ImageFile;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.service.TripImageUpdateService;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripImage;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripImageUpdateService(여행 이미지 변경 서비스) 테스트")
public class TripImageUpdateServiceTest {

    @InjectMocks
    private TripImageUpdateService tripImageUpdateService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripImageOutputAdapter tripImageOutputAdapter;

    private static final String TEST_RESOURCE_PATH = "src/test/resources/testFiles/";

    @DisplayName("여행 이미지 변경 성공 테스트")
    @Test
    public void successTest() throws Exception {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        ImageFile imageFile = imageFileFixture("test-jpeg-image.jpeg");

        Trip trip = TripFixture.undecided_Id(tripId, tripperId);
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        willDoNothing().given(tripImageOutputAdapter).uploadImage(any(ImageFile.class), anyString());


        String fullPath = String.format("https://{여행 이미지 저장소}/trips/%d/{uuid 파일명}.jpeg", tripId);
        given(tripImageOutputAdapter.getFullTripImageURL(anyString())).willReturn(fullPath);

        // when
        String returnFullPath = tripImageUpdateService.updateTripImage(tripId, tripperId, imageFile);

        // then
        assertThat(trip.getTripImage()).isNotEqualTo(TripImage.defaultImage());
        assertThat(returnFullPath).isEqualTo(fullPath);
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(tripImageOutputAdapter, times(1)).uploadImage(any(ImageFile.class), anyString());
        verify(tripImageOutputAdapter, times(1)).getFullTripImageURL(anyString());
    }

    @DisplayName("일치하는 식별자의 여행이 없으면 -> TripNotFoundException")
    @Test
    public void tripNotFoundTest() throws Exception {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        ImageFile imageFile = imageFileFixture("test-jpeg-image.jpeg");

        given(tripRepository.findById(eq(tripId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->tripImageUpdateService.updateTripImage(tripId, tripperId, imageFile))
                .isInstanceOf(TripNotFoundException.class);
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(tripImageOutputAdapter, times(0)).uploadImage(any(ImageFile.class), anyString());
    }

    @DisplayName("수정할 권한 없는 사용자 -> NoTripUpdateAuthorityException")
    @Test
    public void testNoTripUpdateAuthorityTripper() throws Exception {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;
        Long invalidTripperId = 3L;

        ImageFile imageFile = imageFileFixture("test-jpeg-image.jpeg");

        Trip trip = TripFixture.undecided_Id(tripId, tripperId);
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        // when & then
        assertThatThrownBy(() ->tripImageUpdateService.updateTripImage(tripId, invalidTripperId, imageFile))
                .isInstanceOf(NoTripUpdateAuthorityException.class);
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(tripImageOutputAdapter, times(0)).uploadImage(any(ImageFile.class), anyString());
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
