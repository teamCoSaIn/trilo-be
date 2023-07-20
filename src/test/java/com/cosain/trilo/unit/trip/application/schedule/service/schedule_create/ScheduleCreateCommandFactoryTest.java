package com.cosain.trilo.unit.trip.application.schedule.service.schedule_create;

import com.cosain.trilo.common.exception.CustomException;
import com.cosain.trilo.common.exception.CustomValidationException;
import com.cosain.trilo.trip.application.exception.NullTripIdException;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_create.ScheduleCreateCommandFactory;
import com.cosain.trilo.trip.domain.exception.InvalidCoordinateException;
import com.cosain.trilo.trip.domain.exception.InvalidScheduleTitleException;
import com.cosain.trilo.trip.domain.vo.Coordinate;
import com.cosain.trilo.trip.domain.vo.Place;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@DisplayName("ScheduleCreateCommandFactory 테스트")
public class ScheduleCreateCommandFactoryTest {

    private ScheduleCreateCommandFactory scheduleCreateCommandFactory = new ScheduleCreateCommandFactory();


    @DisplayName("(표준 성공 테스트) 정상 입력 -> command 정상 생성")
    @Test
    void normalSuccessTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 37.11924;
        Double longitude = 123.1274;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        ScheduleCreateCommand command = scheduleCreateCommandFactory.createCommand(
                dayId, tripId, rawScheduleTitle,
                placeId, placeName,
                latitude, longitude, exceptions);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getDayId()).isEqualTo(dayId);
        assertThat(command.getTripId()).isEqualTo(tripId);
        assertThat(command.getScheduleTitle()).isEqualTo(ScheduleTitle.of(rawScheduleTitle));
        assertThat(command.getPlace()).isEqualTo(Place.of(placeId, placeName, Coordinate.of(latitude, longitude)));
    }

    @DisplayName("tripId null -> 검증 예외 발생")
    @Test
    void nullTripIdTest() {
        // given
        Long dayId = 1L;
        Long tripId = null;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 39.123;
        Double longitude = 123.7712;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(NullTripIdException.class);
    }

    @DisplayName("일정 제목이 null 아니고 20자 이하(공백 허용) -> 정상 생성")
    @ValueSource(strings = {"일정 제목", "", "     "})
    @ParameterizedTest
    void scheduleTitleSuccessTest(String rawScheduleTitle) {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 39.123;
        Double longitude = 123.7712;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        ScheduleCreateCommand command = scheduleCreateCommandFactory.createCommand(
                dayId, tripId, rawScheduleTitle,
                placeId, placeName,
                latitude, longitude, exceptions);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getDayId()).isEqualTo(dayId);
        assertThat(command.getTripId()).isEqualTo(tripId);
        assertThat(command.getScheduleTitle()).isEqualTo(ScheduleTitle.of(rawScheduleTitle));
        assertThat(command.getPlace()).isEqualTo(Place.of(placeId, placeName, Coordinate.of(latitude, longitude)));
    }

    @DisplayName("일정 제목 null -> 검증 예외 발생")
    @Test
    void nullScheduleTitleTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = null; // 일정 제목이 null
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 39.123;
        Double longitude = 123.7712;
        List<CustomException> exceptions = new ArrayList<>();


        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTitleException.class);
    }

    @DisplayName("제목이 35자보다 긴 문자열 -> 검증 예외 발생")
    @Test
    void tooLongScheduleTitleTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "가".repeat(36);
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 39.123;
        Double longitude = 123.7712;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidScheduleTitleException.class);
    }

    @DisplayName("위도 누락 -> 검증 예외 발생")
    @Test
    void nullLatitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = null;
        Double longitude = 123.7712;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidCoordinateException.class);
    }

    @DisplayName("경도 누락 -> 검증 예외 발생")
    @Test
    void nullLongitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 39.123;
        Double longitude = null;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidCoordinateException.class);
    }

    @DisplayName("위도, 경도 누락 -> 검증 예외 발생")
    @Test
    void nullLatitudeAndLongitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = null;
        Double longitude = null;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidCoordinateException.class);
    }

    @DisplayName("위도가 제한보다 작은 값 -> 검증 예외 발생")
    @Test
    void tooSmallLatitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = Coordinate.MIN_LATITUDE - 0.001;
        Double longitude = 127.123;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidCoordinateException.class);
    }

    @DisplayName("위도가 제한보다 큰 값 -> 검증 예외 발생")
    @Test
    void tooBigLatitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = Coordinate.MAX_LATITUDE + 0.001;
        Double longitude = 127.123;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidCoordinateException.class);
    }

    @DisplayName("경도가 제한보다 작은 값 -> 검증 예외 발생")
    @Test
    void tooSmallLongitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 37.1234;
        Double longitude = Coordinate.MIN_LONGITUDE - 0.001;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidCoordinateException.class);
    }

    @DisplayName("경도가 제한보다 큰 값 -> 검증 예외 발생")
    @Test
    void tooBigLongitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = 1L;
        String rawScheduleTitle = "일정 제목";
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = 37.1234;
        Double longitude = Coordinate.MAX_LONGITUDE + 0.001;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(1);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(InvalidCoordinateException.class);
    }

    @DisplayName("TripId, 제목 누락, 위도 누락 -> 검증 예외 발생")
    @Test
    void nullTripId_And_NullTitle_And_NullLatitudeTest() {
        // given
        Long dayId = 1L;
        Long tripId = null;
        String rawScheduleTitle = null;
        String placeId = "place-id";
        String placeName = "place-name";
        Double latitude = null;
        Double longitude = 37.124;
        List<CustomException> exceptions = new ArrayList<>();

        // when
        CustomValidationException cve = catchThrowableOfType(
                () -> scheduleCreateCommandFactory.createCommand(
                        dayId, tripId, rawScheduleTitle,
                        placeId, placeName,
                        latitude, longitude, exceptions),
                CustomValidationException.class);

        // then
        assertThat(cve).isNotNull();
        assertThat(cve.getExceptions()).hasSize(3);
        assertThat(cve.getExceptions().get(0)).isInstanceOf(NullTripIdException.class);
        assertThat(cve.getExceptions().get(1)).isInstanceOf(InvalidScheduleTitleException.class);
        assertThat(cve.getExceptions().get(2)).isInstanceOf(InvalidCoordinateException.class);
    }

}
