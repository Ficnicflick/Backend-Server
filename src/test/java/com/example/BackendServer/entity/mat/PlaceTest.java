package com.example.BackendServer.entity.mat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceTest {

    @Test
    @DisplayName("위도와 경도로 Place 찾기")
    void getLocation(){
        //given

        Place place = Place.TTUKSEOM_HAN_RIVER;

        //when
        Place findPlace = Place.getLocation(100, 200);
        //then
        Assertions.assertThat(place).isEqualTo(findPlace);

    }

}