package com.cosain.trilo.unit.trip.domain.vo;

import com.cosain.trilo.trip.domain.exception.InvalidCoordinateException;
import com.cosain.trilo.trip.domain.vo.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DisplayName("[TripCommand] Coordinate 테스트")
public class CoordinateTest {

    @Nested
    @DisplayName("Coordinate 생성 테스트")
    class CreateCoordinate_from_of_method {

        @Test
        @DisplayName("위도가 누락되면 InvalidCoordinateException 발생")
        public void nullLatitudeTest() {
            // given
            Double latitude =  null;
            Double longitude = 123.771;

            // when & then
            assertThatThrownBy(() -> Coordinate.of(latitude, longitude))
                    .isInstanceOf(InvalidCoordinateException.class);
        }

        @Test
        @DisplayName("경도가 누락되면 InvalidCoordinateException 발생")
        public void nullLongitudeTest() {
            // given
            Double latitude =  37.1246;
            Double longitude = null;

            // when & then
            assertThatThrownBy(() -> Coordinate.of(latitude, longitude))
                    .isInstanceOf(InvalidCoordinateException.class);
        }

        @Test
        @DisplayName("위도, 경도가 모두 누락되면 InvalidCoordinateException 발생")
        public void nullLatitudeAndLongitudeTest() {
            // given
            Double latitude =  null;
            Double longitude = null;

            // when & then
            assertThatThrownBy(() -> Coordinate.of(latitude, longitude))
                    .isInstanceOf(InvalidCoordinateException.class);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-255, -123.12, -91, -90.001, 90.001, 91, 100.123, 255})
        @DisplayName("위도의 범위가 -90보다 작거나, 90보다 크면 InvalidCoordinateException이 발생한다.")
        public void when_invalid_latitude_it_throws_InvalidCoordinateException(double invalidLatitude) {
            // given
            double longitude = 135.172;

            // when & then
            assertThatThrownBy(() -> Coordinate.of(invalidLatitude, longitude))
                    .isInstanceOf(InvalidCoordinateException.class);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-65535, -255, -180.0001, -180.0001, 180.0001, 182, 255, 65535})
        @DisplayName("위도의 범위가 -180보다 작거나, 180보다 크면 InvalidLatitudeException이 발생한다.")
        public void when_invalid_longitude_it_throws_InvalidCoordinateException(double invalidLongitude) {
            // given
            double latitude = 35.72221;

            // when & then
            assertThatThrownBy(() -> Coordinate.of(latitude, invalidLongitude))
                    .isInstanceOf(InvalidCoordinateException.class);
        }


        @Test
        @DisplayName("위도 범위와 경도범위가 올바르다면, 정상적으로 Coordinate가 생성된다.")
        public void createSuccess() {
            // given
            double latitude = 37.72221;
            double longitude = 137.86523;

            // when
            var coordinate = Coordinate.of(latitude, longitude);
            log.info("coordinate = {}", coordinate);

            // then
            assertThat(coordinate).isNotNull();
            assertThat(coordinate.getLatitude()).isEqualTo(latitude);
            assertThat(coordinate.getLongitude()).isEqualTo(longitude);
        }
    }
}
