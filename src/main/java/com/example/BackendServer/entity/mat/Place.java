package com.example.BackendServer.entity.mat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Place {
    TTUKSEOM_HAN_RIVER("뚝섬 한강", 100, 200);

    private final String location;
    private final double logitude;
    private final double lantitude;

    public static Place getLocation(double logitude, double lantitude){
        Place place1 = Arrays
                .stream(Place.values()).filter(place -> place.getLogitude() == logitude && place.getLantitude() == lantitude)
                .collect(Collectors.toList()).stream().findFirst().get();

        return place1;
    }
}
