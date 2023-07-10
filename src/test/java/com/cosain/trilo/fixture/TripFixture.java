package com.cosain.trilo.fixture;

import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class TripFixture {

    /**
     * 식별자가 있는 Undecided 상태의 여행을 생성합니다. 여행의 제목은 디폴트 값이 지정됩니다.
     *
     * @param tripId    : 여행 식별자
     * @param tripperId : 소유 여행자(사용자)의 식별자
     * @return 여행
     */
    public static Trip undecided_Id(Long tripId, Long tripperId) {
        return undecided_Id_Title(tripId, tripperId, "여행 제목");
    }

    /**
     * 식별자가 있는 Undecided 상태의 여행을 생성합니다.
     *
     * @param tripId    : 여행 식별자
     * @param tripperId : 소유 여행자(사용자)의 식별자
     * @param rawTitle  : 제목(일반 문자열)
     * @return 여행
     */
    public static Trip undecided_Id_Title(Long tripId, Long tripperId, String rawTitle) {
        return createMockTrip(tripId, tripperId, rawTitle, TripStatus.UNDECIDED, TripPeriod.empty());
    }

    /**
     * 식별자가 없는 Undecided 상태의 여행을 생성합니다. 이 때 여행 제목은 디폴트로 지정됩니다.
     *
     * @param tripperId : 소유 여행자(사용자)의 식별자
     * @return 여행
     */
    public static Trip undecided_nullId(Long tripperId) {
        return undecided_nullId_Title(tripperId, "여행 제목");
    }

    /**
     * 식별자가 없는 Undecided 상태의 여행을 생성합니다.
     *
     * @param tripperId : 소유 여행자(사용자)의 식별자
     * @param rawTitle  : 제목(일반 문자열)
     * @return 여행
     */
    public static Trip undecided_nullId_Title(Long tripperId, String rawTitle) {
        return createMockTrip(null, tripperId, rawTitle, TripStatus.UNDECIDED, TripPeriod.empty());
    }

    /**
     * 식별자가 있는 Decided 상태의 여행을 생성합니다. 기간에 속한 Day들의 Color들은 디폴트 색상이 적용됩니다.
     * @param tripId : 여행 식별자
     * @param tripperId : 여행자(사용자) 식별자
     * @param startDate : 여행 시작일
     * @param endDate : 여행 종료일
     * @param startDayId : 생성되는 Day들의 시작 id
     * @return 여행(+ 기간에 속하는 Day들을 포함시킴)
     */
    public static Trip decided_Id(Long tripId, Long tripperId, LocalDate startDate, LocalDate endDate, Long startDayId) {
        return decided_Id_Color(tripId, tripperId, startDate, endDate, startDayId, DayColor.BLACK);
    }

    /**
     * 식별자가 있는 Decided 상태의 여행을 생성합니다.
     * @param tripId : 여행 식별자
     * @param tripperId : 여행자(사용자) 식별자
     * @param startDate : 여행 시작일
     * @param endDate : 여행 종료일
     * @param startDayId : 생성되는 Day들의 시작 id
     * @param dayColor : 소속된 Day들 전체의 색상
     * @return 여행(+ 기간에 속하는 Day들을 포함시킴)
     */
    public static Trip decided_Id_Color(Long tripId, Long tripperId, LocalDate startDate, LocalDate endDate, Long startDayId, DayColor dayColor) {
        TripPeriod tripPeriod = TripPeriod.of(startDate, endDate);
        Trip trip = createMockTrip(tripId, tripperId, "여행 제목", TripStatus.DECIDED, tripPeriod);

        List<LocalDate> dates = tripPeriod.dateStream().toList();

        List<Day> tripDays = IntStream.range(0, dates.size())
                .mapToObj(i -> createMockDay(startDayId + i, dates.get(i), trip, dayColor))
                .toList();
        trip.getDays().addAll(tripDays);
        return trip;
    }

    /**
     * 식별자가 없는 Decided 상태의 여행을 생성합니다. 기간에 속한 Day들의 Color들은 디폴트 색상이 적용됩니다.
     * @param tripperId : 여행자(사용자) 식별자
     * @param startDate : 여행 시작일
     * @param endDate : 여행 종료일
     * @return 여행(+ 기간에 속하는 Day들을 포함시킴)
     */
    public static Trip decided_nullId(Long tripperId, LocalDate startDate, LocalDate endDate) {
        return decided_nullId_Color(tripperId, startDate, endDate, DayColor.BLACK);
    }

    /**
     * 식별자가 없는 Decided 상태의 여행을 생성합니다.
     * @param tripperId : 여행자(사용자) 식별자
     * @param startDate : 여행 시작일
     * @param endDate : 여행 종료일
     * @param dayColor : 소속된 Day들 전체의 색상
     * @return 여행(+ 기간에 속하는 Day들을 포함시킴)
     */
    public static Trip decided_nullId_Color(Long tripperId, LocalDate startDate, LocalDate endDate, DayColor dayColor) {
        TripPeriod tripPeriod = TripPeriod.of(startDate, endDate);
        Trip trip = createMockTrip(null, tripperId, "여행 제목", TripStatus.DECIDED, tripPeriod);

        List<Day> tripDays = tripPeriod.dateStream()
                .map(date -> createMockDay(null, date, trip, dayColor))
                .toList();
        trip.getDays().addAll(tripDays);
        return trip;
    }

    private static Trip createMockTrip(Long tripId, Long tripperId, String rawTitle, TripStatus tripStatus, TripPeriod tripPeriod) {
        return Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of(rawTitle))
                .status(tripStatus)
                .tripPeriod(tripPeriod)
                .tripImage(TripImage.defaultImage())
                .build();
    }

    private static Day createMockDay(Long dayId, LocalDate tripDate, Trip trip, DayColor dayColor) {
        return Day.builder()
                .id(dayId)
                .tripDate(tripDate)
                .trip(trip)
                .dayColor(dayColor)
                .build();
    }

}
