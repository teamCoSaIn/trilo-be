package com.cosain.trilo.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.dto.ChangeTripPeriodResult;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Slf4j
@Table(name = "trip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id;

    @Column(name = "tripper_id")
    private Long tripperId;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_status")
    private TripStatus status;

    @Embedded
    private TripPeriod tripPeriod;

    @OneToMany(mappedBy = "trip")
    private final List<Day> days = new ArrayList<>();

    /**
     * 여행(Trip)을 최초로 생성합니다. 최초 생성된 Trip은 UNDECIDED 상태입니다.
     * @param title: 여행의 제목
     * @param tripperId : 여행자의 식별자
     * @return 생성된 Trip
     */
    public static Trip create(String title, Long tripperId) {
        return Trip.builder()
                .tripperId(tripperId)
                .title(title)
                .status(TripStatus.UNDECIDED)
                .tripPeriod(null)
                .build();
    }

    /**
     * 테스트의 편의성을 위해 Builder accessLevel = PUBLIC 으로 설정
     */
    @Builder(access = AccessLevel.PUBLIC)
    private Trip(Long id, Long tripperId, String title, TripStatus status, TripPeriod tripPeriod, List<Day> days) {
        this.id = id;
        this.tripperId = tripperId;
        this.title = title;
        this.status = status;
        this.tripPeriod = tripPeriod;

        if (days != null) {
            this.days.addAll(days);
        }
    }

    /**
     * Trip의 제목을 변경합니다.
     * @param newTitle : 변경할 제목
     */
    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * 기간을 변경합니다. 이에 따라 삭제되는 날짜와, 생성되는 날짜들을 각각 리스트로 반환합니다.
     * @param startDate
     * @param endDate
     * @return ChangeTripPeriodResult
     */
    public ChangeTripPeriodResult updatePeriod(LocalDate startDate, LocalDate endDate){
        List<Day> overlappedDays = getNotOverlappedDays(startDate, endDate);
        this.days.removeAll(overlappedDays);

        List<Day> newDays = new ArrayList<>();
        if(status.equals(TripStatus.UNDECIDED) || !isOverlapped(startDate, endDate)){
            newDays = createDays(startDate, endDate);
        }else{
            if(startDate.isBefore(tripPeriod.getStartDate())){
                newDays.addAll(createDays(startDate, tripPeriod.getStartDate().minusDays(1)));
            }

            if(endDate.isAfter(tripPeriod.getEndDate())){
                newDays.addAll(createDays(tripPeriod.getEndDate().plusDays(1), endDate));
            }
        }
        this.days.addAll(newDays);
        this.tripPeriod = TripPeriod.of(startDate, endDate);
        this.status = TripStatus.DECIDED;
        return ChangeTripPeriodResult.of(overlappedDays, newDays);
    }

    public List<Day> getNotOverlappedDays(LocalDate startDate, LocalDate endDate){
        if(status.equals(TripStatus.UNDECIDED)) return new ArrayList<>(); // 기간이 안정해진 상태
        // 기간이 이미 잡혀있는 상태에서, 둘다 null인 날짜를 전달해서 겹치지 않는 날짜를 얻으려는 시도를 함

        List<Day> removeDays = new ArrayList<>();
        for (Day day : days) {
            LocalDate date = day.getTripDate();
            if (date.isBefore(startDate) || date.isAfter(endDate)) {
                removeDays.add(day);
            }
        }
        return removeDays;
    }

    private boolean isOverlapped(LocalDate startDate, LocalDate endDate){
        if(startDate.isAfter(tripPeriod.getEndDate()) || endDate.isBefore(tripPeriod.getStartDate())) return false;
        return true;
    }

    private List<Day> createDays(LocalDate startDate, LocalDate endDate){
        List<Day> days = new ArrayList<>();
        LocalDate currendDate = startDate;

        while(!currendDate.isAfter(endDate)){
            days.add(Day.of(currendDate, this));
            currendDate = currendDate.plusDays(1);
        }

        return days;
    }

}
