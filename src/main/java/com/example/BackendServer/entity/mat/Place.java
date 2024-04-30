package com.example.BackendServer.entity.mat;

import com.example.BackendServer.common.exception.BaseException;
import com.example.BackendServer.common.response.BaseResponseStatus;
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

        return Arrays
                .stream(Place.values()).filter(place -> place.getLogitude() == logitude && place.getLantitude() == lantitude)
                .collect(Collectors.toList()).stream().findFirst().orElseThrow(
                        () -> new BaseException(BaseResponseStatus.NOT_EXIST_PLACE)
                );
    }
}
