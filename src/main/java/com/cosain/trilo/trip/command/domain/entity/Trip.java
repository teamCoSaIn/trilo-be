package com.cosain.trilo.trip.command.domain.entity;

import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(name = "trip")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "tripperId", "title", "status", "tripPeriod"})
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
    private TripStatus status;

    @Embedded
    private TripPeriod tripPeriod;

    /**
     * 여행(Trip)을 최초로 생성합니다. 최초 생성된 Trip은 UNDECIDED 상태입니다.
     * @param title: 여행의 제목
     * @param tripperId : 여행자의 식별자
     * @return 생성된 Trip
     */
    public static Trip create(String title, Long tripperId) {
        return new Trip(tripperId, title, TripStatus.UNDECIDED, null);
    }

    private Trip(Long tripperId, String title, TripStatus status, TripPeriod tripPeriod) {
        this.tripperId = tripperId;
        this.title = title;
        this.status = status;
        this.tripPeriod = tripPeriod;
    }

    /**
     * 여행의 제목을 변경합니다.
     * @param newTitle : 변경할 제목
     * @return 제목이 기존과 달라졌는 지 여부
     */
    public boolean changeTitle(String newTitle) {
        if (newTitle.equals(this.title)) {
            return false;
        }
        this.title = newTitle;
        return true;
    }

}
