package com.cosain.trilo.trip.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "likes")
@EqualsAndHashCode(of = {"tripperId", "tripId"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "tripper_id")
    private Long tripperId;

    @Column(name = "trip_id")
    private Long tripId;

    private Like(Long tripId, Long tripperId){
        this.tripId = tripId;
        this.tripperId = tripperId;
    }

    public static Like of(Long tripId, Long tripperId){
        return new Like(tripId, tripperId);
    }

}
