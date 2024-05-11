package com.example.BackendServer.entity.mat;

import jakarta.validation.constraints.Size;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceTest {

    @Test
    @DisplayName("위도와 경도로 Place 찾기")
    void getLocation(){
        //given

        double logitude = 127.0699562;

        double lantitude = 37.5293507;

        Place place = Place.TTUKSEOM_HAN_RIVER1;
        System.out.println(place.getLocation());
        System.out.println(place.getLatitude());

        //when
        Place findPlace = Place.getLocation(place.getPlaceId());
        //then
        Assertions.assertThat(place).isEqualTo(findPlace);

    }

}