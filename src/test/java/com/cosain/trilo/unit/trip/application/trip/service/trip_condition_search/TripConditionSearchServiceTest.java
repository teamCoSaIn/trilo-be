package com.cosain.trilo.unit.trip.application.trip.service.trip_condition_search;

import com.cosain.trilo.trip.application.dao.TripQueryDAO;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripConditionSearchService;
import com.cosain.trilo.trip.application.trip.service.trip_condition_search.TripSearchResponse;
import com.cosain.trilo.trip.infra.adapter.TripImageOutputAdapter;
import com.cosain.trilo.trip.presentation.trip.dto.request.TripSearchRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripConditionSearchServiceTest {

    @InjectMocks
    private TripConditionSearchService tripConditionSearchService;
    @Mock
    private TripImageOutputAdapter tripImageOutputAdapter;
    @Mock
    private TripQueryDAO tripQueryDAO;

    @Test
    void 여행_목록_조회_기능_호출테스트(){
        // given
        String imageName = "image.jpg";
        String imageURL = "https://.../image.jpg";
        TripSearchRequest tripSearchRequest = new TripSearchRequest("제주", "RECENT", 3, 1L);
        TripSearchResponse.TripSummary tripSummary1 = new TripSearchResponse.TripSummary(2L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주도 여행", imageName);
        TripSearchResponse.TripSummary tripSummary2 = new TripSearchResponse.TripSummary(1L, 1L, LocalDate.of(2023, 4, 4), LocalDate.of(2023, 4, 10), "제주 가보자", imageName);
        TripSearchResponse tripSearchResponse = new TripSearchResponse(true, List.of(tripSummary1, tripSummary2));

        given(tripQueryDAO.findWithSearchConditions(eq(tripSearchRequest))).willReturn(tripSearchResponse);
        given(tripImageOutputAdapter.getFullTripImageURL(eq(imageName))).willReturn(imageURL);

        // when
        tripConditionSearchService.findBySearchConditions(tripSearchRequest);

        // then
        verify(tripQueryDAO, times(1)).findWithSearchConditions(eq(tripSearchRequest));
        verify(tripImageOutputAdapter, times(2)).getFullTripImageURL(eq(imageName));
    }

}
